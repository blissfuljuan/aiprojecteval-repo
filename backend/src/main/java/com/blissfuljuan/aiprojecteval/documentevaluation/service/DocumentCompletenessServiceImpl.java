package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.ClassAssignmentCompletenessSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentCompletenessReportResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementCompletenessItemResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentCompletenessFileSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.submission.service.SubmissionQueryService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentCompletenessServiceImpl implements DocumentCompletenessService {

	private static final List<SubmissionStatus> SATISFYING_SUBMISSION_STATUSES = List.of(
			SubmissionStatus.SUBMITTED,
			SubmissionStatus.RESUBMITTED,
			SubmissionStatus.ACCEPTED);

	private static final List<SubmissionFileStatus> INACTIVE_FILE_STATUSES = List.of(
			SubmissionFileStatus.REPLACED,
			SubmissionFileStatus.REMOVED,
			SubmissionFileStatus.INVALID);

	private final DocumentRequirementSetAssignmentRepository assignmentRepository;
	private final DocumentRequirementRepository requirementRepository;
	private final SubmissionQueryService submissionQueryService;
	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;

	DocumentCompletenessServiceImpl(
			DocumentRequirementSetAssignmentRepository assignmentRepository,
			DocumentRequirementRepository requirementRepository,
			SubmissionQueryService submissionQueryService,
			UserRepository userRepository,
			ProjectRepository projectRepository) {
		this.assignmentRepository = assignmentRepository;
		this.requirementRepository = requirementRepository;
		this.submissionQueryService = submissionQueryService;
		this.userRepository = userRepository;
		this.projectRepository = projectRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentCompletenessReportResponse getMyCompletenessReport(String currentUserEmail, Long assignmentId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(assignmentId);
		List<SubmissionResponse> submissions = submissionQueryService
				.findSubmissionsByTypeAndAssignmentAndSubmitter(SubmissionType.DOCUMENT, assignmentId, currentUser.getId());
		return buildReport(assignment, currentUser, null, submissions);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentCompletenessReportResponse getUserCompletenessReport(
			String currentUserEmail,
			Long assignmentId,
			Long userId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(assignmentId);
		checkCanManageAssignment(currentUser, assignment);
		User submitter = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		List<SubmissionResponse> submissions = submissionQueryService
				.findSubmissionsByTypeAndAssignmentAndSubmitter(SubmissionType.DOCUMENT, assignmentId, userId);
		return buildReport(assignment, submitter, null, submissions);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentCompletenessReportResponse getProjectCompletenessReport(
			String currentUserEmail,
			Long projectId,
			Long assignmentId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(assignmentId);
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
		if (assignment.getProject() == null || !projectId.equals(assignment.getProject().getId())) {
			throw new BadRequestException("Requirement set assignment is not assigned to this project");
		}
		if (!canManageAssignment(currentUser, assignment) && !isProjectOwner(currentUser, project)) {
			throw new AccessDeniedException("Access denied");
		}

		List<SubmissionResponse> submissions = submissionQueryService
				.findSubmissionsByTypeAndAssignmentAndProject(SubmissionType.DOCUMENT, assignmentId, projectId);
		return buildReport(assignment, null, project, submissions);
	}

	@Override
	@Transactional(readOnly = true)
	public ClassAssignmentCompletenessSummaryResponse getClassCompletenessSummary(
			String currentUserEmail,
			Long courseClassId,
			Long assignmentId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(assignmentId);
		checkCanManageAssignment(currentUser, assignment);
		if (assignment.getCourseClass() == null || !courseClassId.equals(assignment.getCourseClass().getId())) {
			throw new BadRequestException("Requirement set assignment is not assigned to this class");
		}

		List<SubmissionResponse> submissions = submissionQueryService
				.findSubmissionsByTypeAndAssignmentAndClass(SubmissionType.DOCUMENT, assignmentId, courseClassId);
		Map<Long, List<SubmissionResponse>> submissionsBySubmitter = new LinkedHashMap<>();
		for (SubmissionResponse submission : submissions) {
			if (submission.submittedById() == null) {
				continue;
			}
			submissionsBySubmitter
					.computeIfAbsent(submission.submittedById(), ignored -> new ArrayList<>())
					.add(submission);
		}

		// TODO: include students with no submissions once class enrollment/member records are available.
		List<DocumentCompletenessReportResponse> reports = submissionsBySubmitter.values()
				.stream()
				.map(groupedSubmissions -> buildReport(
						assignment,
						findUserById(groupedSubmissions.get(0).submittedById()),
						null,
						groupedSubmissions))
				.toList();

		long completeCount = reports.stream().filter(DocumentCompletenessReportResponse::complete).count();
		long readyForEvaluationCount = reports.stream()
				.filter(DocumentCompletenessReportResponse::readyForEvaluation)
				.count();
		DocumentRequirementSet requirementSet = assignment.getRequirementSet();
		CourseClass courseClass = assignment.getCourseClass();

		return new ClassAssignmentCompletenessSummaryResponse(
				courseClassId,
				courseClass == null ? null : courseClass.getName(),
				assignmentId,
				requirementSet == null ? null : requirementSet.getId(),
				requirementSet == null ? null : requirementSet.getName(),
				reports.size(),
				(int) completeCount,
				reports.size() - (int) completeCount,
				(int) readyForEvaluationCount,
				reports);
	}

	private DocumentCompletenessReportResponse buildReport(
			DocumentRequirementSetAssignment assignment,
			User submitter,
			Project reportProject,
			List<SubmissionResponse> submissions) {
		DocumentRequirementSet requirementSet = assignment.getRequirementSet();
		List<DocumentRequirement> requirements = requirementRepository
				.findByRequirementSetIdOrderBySortOrderAsc(requirementSet.getId());
		Map<Long, SubmissionResponse> latestSubmissionByRequirement = latestSubmissionByRequirement(submissions);

		List<String> blockingIssues = new ArrayList<>();
		if (assignment.getStatus() != RequirementSetAssignmentStatus.ACTIVE) {
			blockingIssues.add("Requirement set assignment is not active.");
		}
		if (requirementSet.getStatus() == ConfigurationStatus.ARCHIVED) {
			blockingIssues.add("Requirement set is archived.");
		} else if (requirementSet.getStatus() != ConfigurationStatus.ACTIVE) {
			blockingIssues.add("Requirement set is not active.");
		}

		List<DocumentRequirementCompletenessItemResponse> items = requirements.stream()
				.map(requirement -> buildRequirementItem(
						requirement,
						latestSubmissionByRequirement.get(requirement.getId()),
						blockingIssues))
				.toList();

		int totalRequirements = items.size();
		int requiredRequirements = (int) items.stream().filter(DocumentRequirementCompletenessItemResponse::required).count();
		int satisfiedRequiredRequirements = (int) items.stream()
				.filter(DocumentRequirementCompletenessItemResponse::required)
				.filter(DocumentRequirementCompletenessItemResponse::satisfied)
				.count();
		int missingRequiredRequirements = (int) items.stream()
				.filter(DocumentRequirementCompletenessItemResponse::required)
				.filter(item -> !item.submitted())
				.count();
		int incompleteRequiredRequirements = (int) items.stream()
				.filter(DocumentRequirementCompletenessItemResponse::required)
				.filter(DocumentRequirementCompletenessItemResponse::submitted)
				.filter(item -> !item.satisfied())
				.count();

		boolean activeConfiguration = assignment.getStatus() == RequirementSetAssignmentStatus.ACTIVE
				&& requirementSet.getStatus() == ConfigurationStatus.ACTIVE;
		boolean complete = activeConfiguration
				&& requiredRequirements == satisfiedRequiredRequirements
				&& items.stream()
						.filter(DocumentRequirementCompletenessItemResponse::required)
						.noneMatch(DocumentRequirementCompletenessItemResponse::blocking);
		boolean readyForEvaluation = complete && blockingIssues.isEmpty();
		Project project = reportProject == null ? assignment.getProject() : reportProject;
		CourseClass courseClass = assignment.getCourseClass();

		return new DocumentCompletenessReportResponse(
				assignment.getId(),
				requirementSet.getId(),
				requirementSet.getName(),
				assignment.getAssignmentType() == null ? null : assignment.getAssignmentType().name(),
				assignment.getStatus() == null ? null : assignment.getStatus().name(),
				courseClass == null ? null : courseClass.getId(),
				courseClass == null ? null : courseClass.getName(),
				project == null ? null : project.getId(),
				project == null ? null : project.getTitle(),
				submitter == null ? null : submitter.getId(),
				submitter == null ? null : formatUserName(submitter),
				totalRequirements,
				requiredRequirements,
				totalRequirements - requiredRequirements,
				satisfiedRequiredRequirements,
				missingRequiredRequirements,
				incompleteRequiredRequirements,
				complete,
				readyForEvaluation,
				items,
				List.copyOf(blockingIssues),
				LocalDateTime.now());
	}

	private DocumentRequirementCompletenessItemResponse buildRequirementItem(
			DocumentRequirement requirement,
			SubmissionResponse latestSubmission,
			List<String> blockingIssues) {
		List<String> issues = new ArrayList<>();
		List<SubmissionFileResponse> files = latestSubmission == null
				? List.of()
				: latestSubmission.files()
						.stream()
						.sorted(Comparator.comparing(file -> file.id() == null ? 0L : file.id()))
						.toList();

		int uploadedFileCount = (int) files.stream()
				.filter(file -> file.fileStatus() == SubmissionFileStatus.UPLOADED)
				.count();
		int validUploadedFileCount = (int) files.stream()
				.filter(this::isValidUploadedFile)
				.count();
		boolean submitted = latestSubmission != null;
		boolean statusSatisfied = latestSubmission != null
				&& SATISFYING_SUBMISSION_STATUSES.contains(latestSubmission.status());
		boolean hasValidFiles = validUploadedFileCount > 0;
		boolean satisfied = submitted && statusSatisfied && hasValidFiles;

		if (latestSubmission == null) {
			issues.add("Missing required document: " + requirement.getName());
		} else {
			addSubmissionIssues(requirement, latestSubmission, issues, uploadedFileCount, validUploadedFileCount);
		}

		boolean blocking = requirement.isRequired() && !satisfied;
		if (blocking) {
			blockingIssues.addAll(issues.stream()
					.filter(issue -> !issue.isBlank())
					.toList());
		}

		return new DocumentRequirementCompletenessItemResponse(
				requirement.getId(),
				requirement.getName(),
				requirement.getDescription(),
				requirement.isRequired(),
				requirement.getSortOrder(),
				latestSubmission == null ? null : latestSubmission.id(),
				latestSubmission == null ? null : latestSubmission.attemptNumber(),
				latestSubmission == null || latestSubmission.status() == null
						? null : latestSubmission.status().name(),
				uploadedFileCount,
				validUploadedFileCount,
				submitted,
				hasValidFiles,
				satisfied,
				blocking,
				requirement.isRequired() ? List.copyOf(issues) : optionalIssues(issues),
				toFileSummaries(files));
	}

	private void addSubmissionIssues(
			DocumentRequirement requirement,
			SubmissionResponse latestSubmission,
			List<String> issues,
			int uploadedFileCount,
			int validUploadedFileCount) {
		if (latestSubmission.status() == SubmissionStatus.DRAFT) {
			issues.add("Latest submission is still in draft: " + requirement.getName());
		} else if (latestSubmission.status() == SubmissionStatus.ARCHIVED) {
			issues.add("Latest submission is archived: " + requirement.getName());
		} else if (!SATISFYING_SUBMISSION_STATUSES.contains(latestSubmission.status())) {
			issues.add("Latest submission is not ready for evaluation: " + requirement.getName());
		}

		if (latestSubmission.files() == null || latestSubmission.files().isEmpty()) {
			issues.add("Submitted document has no uploaded file: " + requirement.getName());
		} else if (uploadedFileCount == 0 && allFilesInactive(latestSubmission.files())) {
			issues.add("All uploaded files are invalid or removed: " + requirement.getName());
		} else if (uploadedFileCount == 0) {
			issues.add("Submitted document has no uploaded file: " + requirement.getName());
		} else if (validUploadedFileCount == 0) {
			issues.add("Uploaded file metadata is incomplete: " + requirement.getName());
		}
	}

	private boolean allFilesInactive(List<SubmissionFileResponse> files) {
		return files.stream()
				.allMatch(file -> INACTIVE_FILE_STATUSES.contains(file.fileStatus()));
	}

	private List<String> optionalIssues(Collection<String> issues) {
		return issues.stream()
				.map(issue -> issue.replaceFirst("^Missing required document:", "Missing optional document:"))
				.toList();
	}

	private List<DocumentCompletenessFileSummaryResponse> toFileSummaries(List<SubmissionFileResponse> files) {
		return files.stream()
				.map(file -> new DocumentCompletenessFileSummaryResponse(
						file.id(),
						file.originalFileName(),
						file.contentType(),
						file.fileSize(),
						file.fileStatus() == null ? null : file.fileStatus().name(),
						file.checksum(),
						file.uploadedAt()))
				.toList();
	}

	private Map<Long, SubmissionResponse> latestSubmissionByRequirement(List<SubmissionResponse> submissions) {
		Map<Long, SubmissionResponse> latest = new LinkedHashMap<>();
		submissions.stream()
				.filter(submission -> submission.requirementId() != null)
				.sorted(this::compareLatestSubmissionFirst)
				.forEach(submission -> latest.putIfAbsent(
						submission.requirementId(),
						submission));
		return latest;
	}

	private int compareLatestSubmissionFirst(SubmissionResponse left, SubmissionResponse right) {
		int attemptComparison = Integer.compare(
				right.attemptNumber() == null ? 0 : right.attemptNumber(),
				left.attemptNumber() == null ? 0 : left.attemptNumber());
		if (attemptComparison != 0) {
			return attemptComparison;
		}
		LocalDateTime rightUpdated = right.lastUpdatedAt();
		LocalDateTime leftUpdated = left.lastUpdatedAt();
		if (rightUpdated == null && leftUpdated == null) {
			return 0;
		}
		if (rightUpdated == null) {
			return -1;
		}
		if (leftUpdated == null) {
			return 1;
		}
		return rightUpdated.compareTo(leftUpdated);
	}

	private boolean isValidUploadedFile(SubmissionFileResponse file) {
		return file.fileStatus() == SubmissionFileStatus.UPLOADED
				&& hasText(file.originalFileName())
				&& (hasText(file.storedFileName()) || hasText(file.fileUrl()));
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private void checkCanManageAssignment(User currentUser, DocumentRequirementSetAssignment assignment) {
		if (!canManageAssignment(currentUser, assignment)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	private boolean canManageAssignment(User currentUser, DocumentRequirementSetAssignment assignment) {
		if (currentUser.getRole() == Role.ADMIN) {
			return true;
		}
		DocumentRequirementSet requirementSet = assignment.getRequirementSet();
		return currentUser.getRole() == Role.INSTRUCTOR
				&& requirementSet != null
				&& requirementSet.getOwnerInstructor() != null
				&& currentUser.getId().equals(requirementSet.getOwnerInstructor().getId());
	}

	private boolean isProjectOwner(User currentUser, Project project) {
		return project != null && currentUser.getId().equals(project.getOwnerUserId());
	}

	private DocumentRequirementSetAssignment findAssignment(Long id) {
		return assignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Requirement set assignment not found"));
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private User findUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private String formatUserName(User user) {
		StringBuilder name = new StringBuilder(user.getFirstName());
		if (user.getMiddleName() != null && !user.getMiddleName().isBlank()) {
			name.append(' ').append(user.getMiddleName());
		}
		name.append(' ').append(user.getLastName());
		return name.toString();
	}
}
