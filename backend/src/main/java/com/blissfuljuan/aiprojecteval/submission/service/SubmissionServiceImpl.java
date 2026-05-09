package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import com.blissfuljuan.aiprojecteval.submission.dto.request.CreateSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.CreateSubmissionRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.SubmissionFileMetadataRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.UpdateSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.submission.mapper.SubmissionMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.model.SubmissionFile;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.service.IdentityQueryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class SubmissionServiceImpl implements SubmissionService {

	private final SubmissionRepository submissionRepository;
	private final SubmissionFileRepository fileRepository;
	private final DocumentRequirementSetAssignmentRepository assignmentRepository;
	private final DocumentRequirementRepository requirementRepository;
	private final IdentityQueryService identityQueryService;
	private final CourseClassRepository courseClassRepository;

	SubmissionServiceImpl(
			SubmissionRepository submissionRepository,
			SubmissionFileRepository fileRepository,
			DocumentRequirementSetAssignmentRepository assignmentRepository,
			DocumentRequirementRepository requirementRepository,
			IdentityQueryService identityQueryService,
			CourseClassRepository courseClassRepository) {
		this.submissionRepository = submissionRepository;
		this.fileRepository = fileRepository;
		this.assignmentRepository = assignmentRepository;
		this.requirementRepository = requirementRepository;
		this.identityQueryService = identityQueryService;
		this.courseClassRepository = courseClassRepository;
	}

	@Override
	@Transactional
	public SubmissionResponse createDraftSubmission(
			String currentUserEmail,
			CreateSubmissionDraftRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		SubmissionTarget target = validateSubmissionTarget(request.assignmentId(), request.requirementId());
		rejectDuplicateDraft(currentUser, target.assignment(), target.requirement());

		Submission submission = buildSubmission(
				currentUser,
				target,
				request.submissionTitle(),
				request.submissionNotes(),
				SubmissionStatus.DRAFT);

		return SubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional
	public SubmissionResponse createSubmission(
			String currentUserEmail,
			CreateSubmissionRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		SubmissionTarget target = validateSubmissionTarget(request.assignmentId(), request.requirementId());
		rejectDuplicateDraft(currentUser, target.assignment(), target.requirement());
		throw new BadRequestException(
				"Create a draft submission, upload at least one valid file, then submit the draft");
	}

	@Override
	@Transactional
	public SubmissionResponse updateDraftSubmission(
			String currentUserEmail,
			Long submissionId,
			UpdateSubmissionDraftRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		Submission submission = findSubmission(submissionId);
		checkCanUpdateDraft(currentUser, submission);

		submission.setSubmissionTitle(request.submissionTitle());
		submission.setSubmissionNotes(request.submissionNotes());
		if (request.files() != null) {
			submission.getFiles().clear();
			addFileMetadata(submission, request.files());
		}

		return SubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional
	public SubmissionResponse submitDraftSubmission(String currentUserEmail, Long submissionId) {
		User currentUser = findUserByEmail(currentUserEmail);
		Submission submission = findSubmission(submissionId);
		checkOwner(currentUser, submission);
		if (submission.getStatus() != SubmissionStatus.DRAFT) {
			throw new BadRequestException("Only draft submissions can be submitted");
		}
		if (!hasValidUploadedFile(submission)) {
			throw new BadRequestException("Submission must have at least one uploaded file before it can be submitted");
		}

		submission.setStatus(SubmissionStatus.SUBMITTED);
		submission.setSubmittedAt(LocalDateTime.now());

		return SubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmissionSummaryResponse> getMySubmissions(
			String currentUserEmail,
			Long assignmentId,
			Long documentRequirementId,
			SubmissionStatus status,
			Long projectId,
			Long courseClassId) {
		User currentUser = findUserByEmail(currentUserEmail);
		return submissionRepository.findByTypeAndSubmittedByIdOrderByCreatedAtDesc(SubmissionType.DOCUMENT, currentUser.getId())
				.stream()
				.filter(submission -> matchesFilters(
						submission,
						assignmentId,
						documentRequirementId,
						status,
						null,
						projectId,
						courseClassId))
				.map(SubmissionMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SubmissionResponse getSubmissionById(String currentUserEmail, Long submissionId) {
		User currentUser = findUserByEmail(currentUserEmail);
		Submission submission = findSubmission(submissionId);
		checkCanViewSubmission(currentUser, submission);

		return SubmissionMapper.toResponse(submission);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmissionSummaryResponse> getSubmissionsByAssignment(
			String currentUserEmail,
			Long assignmentId,
			Long documentRequirementId,
			SubmissionStatus status,
			Long submittedById,
			Long projectId,
			Long courseClassId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(assignmentId);
		checkCanManageAssignment(currentUser, assignment);

		return submissionRepository.findByTypeAndAssignmentIdOrderByCreatedAtDesc(SubmissionType.DOCUMENT, assignmentId)
				.stream()
				.filter(submission -> matchesFilters(
						submission,
						null,
						documentRequirementId,
						status,
						submittedById,
						projectId,
						courseClassId))
				.map(SubmissionMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmissionSummaryResponse> getSubmissionsByProject(
			String currentUserEmail,
			Long projectId,
			Long documentRequirementId,
			SubmissionStatus status,
			Long submittedById) {
		User currentUser = findUserByEmail(currentUserEmail);
		if (!canViewProjectSubmissions(currentUser, projectId)) {
			throw new AccessDeniedException("Access denied");
		}

		return submissionRepository.findByTypeAndProjectIdOrderByCreatedAtDesc(SubmissionType.DOCUMENT, projectId)
				.stream()
				.filter(submission -> matchesFilters(
						submission,
						null,
						documentRequirementId,
						status,
						submittedById,
						null,
						null))
				.filter(submission -> canViewSubmission(currentUser, submission))
				.map(SubmissionMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmissionSummaryResponse> getSubmissionsByClass(
			String currentUserEmail,
			Long courseClassId,
			Long documentRequirementId,
			SubmissionStatus status,
			Long submittedById) {
		User currentUser = findUserByEmail(currentUserEmail);
		findCourseClass(courseClassId);
		if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.INSTRUCTOR) {
			throw new AccessDeniedException("Access denied");
		}

		return submissionRepository.findByTypeAndCourseClassIdOrderByCreatedAtDesc(SubmissionType.DOCUMENT, courseClassId)
				.stream()
				.filter(submission -> matchesFilters(
						submission,
						null,
						documentRequirementId,
						status,
						submittedById,
						null,
						null))
				.filter(submission -> canViewSubmission(currentUser, submission))
				.map(SubmissionMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional
	public SubmissionResponse archiveSubmission(String currentUserEmail, Long submissionId) {
		User currentUser = findUserByEmail(currentUserEmail);
		Submission submission = findSubmission(submissionId);
		checkCanArchive(currentUser, submission);
		if (submission.getStatus() != SubmissionStatus.ARCHIVED) {
			submission.setStatus(SubmissionStatus.ARCHIVED);
		}

		return SubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional
	public SubmissionResponse addFileMetadataToDraftSubmission(
			String currentUserEmail,
			Long submissionId,
			SubmissionFileMetadataRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		Submission submission = findSubmission(submissionId);
		checkCanUpdateDraft(currentUser, submission);
		submission.addFile(buildFileMetadata(findRequirement(submission.getRequirementId()), request));

		return SubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional
	public SubmissionFileResponse removeFileMetadataFromDraftSubmission(
			String currentUserEmail,
			Long fileId) {
		User currentUser = findUserByEmail(currentUserEmail);
		SubmissionFile file = fileRepository.findById(fileId)
				.orElseThrow(() -> new ResourceNotFoundException("Submission file metadata not found"));
		checkCanUpdateDraft(currentUser, file.getSubmission());
		file.setFileStatus(SubmissionFileStatus.REMOVED);

		return SubmissionMapper.toFileResponse(fileRepository.save(file));
	}

	private Submission buildSubmission(
			User currentUser,
			SubmissionTarget target,
			String submissionTitle,
			String submissionNotes,
			SubmissionStatus status) {
		Submission submission = new Submission();
		submission.setType(SubmissionType.DOCUMENT);
		submission.setAssignmentId(target.assignment().getId());
		submission.setRequirementId(target.requirement().getId());
		submission.setProjectId(target.assignment().getProject() == null ? null : target.assignment().getProject().getId());
		submission.setCourseClassId(target.assignment().getCourseClass() == null ? null : target.assignment().getCourseClass().getId());
		submission.setSubmittedBy(currentUser);
		submission.setStatus(status);
		submission.setSubmissionTitle(submissionTitle);
		submission.setSubmissionNotes(submissionNotes);
		submission.setAttemptNumber(nextAttemptNumber(currentUser, target.assignment(), target.requirement()));
		submission.setLastUpdatedAt(LocalDateTime.now());
		return submission;
	}

	private void addFileMetadata(
			Submission submission,
			List<SubmissionFileMetadataRequest> files) {
		if (files == null) {
			return;
		}
		DocumentRequirement requirement = findRequirement(submission.getRequirementId());
		files.forEach(file -> submission.addFile(buildFileMetadata(requirement, file)));
	}

	private SubmissionFile buildFileMetadata(
			DocumentRequirement requirement,
			SubmissionFileMetadataRequest request) {
		validateFileMetadata(requirement, request);

		SubmissionFile file = new SubmissionFile();
		file.setOriginalFileName(request.originalFileName());
		file.setContentType(request.contentType());
		file.setFileSize(request.fileSize());
		file.setFileExtension(normalizeExtension(request.fileExtension(), request.originalFileName()));
		file.setFileUrl(request.fileUrl());
		file.setStoragePath(request.storagePath());
		file.setChecksum(request.checksum());
		file.setFileStatus(SubmissionFileStatus.PENDING_UPLOAD);
		return file;
	}

	private SubmissionTarget validateSubmissionTarget(Long assignmentId, Long documentRequirementId) {
		DocumentRequirementSetAssignment assignment = findAssignment(assignmentId);
		if (assignment.getStatus() != RequirementSetAssignmentStatus.ACTIVE) {
			throw new BadRequestException("Only active requirement set assignments can receive submissions");
		}

		DocumentRequirement requirement = requirementRepository.findById(documentRequirementId)
				.orElseThrow(() -> new ResourceNotFoundException("Document requirement not found"));
		DocumentRequirementSet requirementSet = assignment.getRequirementSet();
		if (requirement.getRequirementSet() == null
				|| requirementSet == null
				|| !requirement.getRequirementSet().getId().equals(requirementSet.getId())) {
			throw new BadRequestException("Document requirement does not belong to the assigned requirement set");
		}

		return new SubmissionTarget(assignment, requirement);
	}

	private void rejectDuplicateDraft(
			User currentUser,
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement) {
		submissionRepository.findFirstByTypeAndSubmittedByIdAndAssignmentIdAndRequirementIdAndStatus(
						SubmissionType.DOCUMENT,
						currentUser.getId(),
						assignment.getId(),
						requirement.getId(),
						SubmissionStatus.DRAFT)
				.ifPresent(existing -> {
					throw new BadRequestException(
							"A draft submission already exists for this assignment and document requirement");
				});
	}

	private Integer nextAttemptNumber(
			User currentUser,
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement) {
		Integer maxAttemptNumber = submissionRepository.findMaxAttemptNumber(
				SubmissionType.DOCUMENT,
				currentUser.getId(),
				assignment.getId(),
				requirement.getId());
		if (maxAttemptNumber == null) {
			return 1;
		}
		return maxAttemptNumber + 1;
	}

	private void validateFileMetadata(
			DocumentRequirement requirement,
			SubmissionFileMetadataRequest request) {
		String extension = normalizeExtension(request.fileExtension(), request.originalFileName());
		Set<AllowedFileType> allowedFileTypes = requirement.getAllowedFileTypes();
		if (allowedFileTypes != null && !allowedFileTypes.isEmpty()) {
			boolean allowed = allowedFileTypes.stream()
					.anyMatch(fileType -> fileType.name().equalsIgnoreCase(extension));
			if (!allowed) {
				throw new BadRequestException("File type is not allowed for this document requirement");
			}
		}
		// TODO: enforce max file size when DocumentRequirement defines a file size limit.
	}

	private String normalizeExtension(String fileExtension, String originalFileName) {
		String extension = fileExtension;
		if ((extension == null || extension.isBlank()) && originalFileName != null) {
			int dotIndex = originalFileName.lastIndexOf('.');
			if (dotIndex >= 0 && dotIndex < originalFileName.length() - 1) {
				extension = originalFileName.substring(dotIndex + 1);
			}
		}
		if (extension == null || extension.isBlank()) {
			return "";
		}
		return extension.replaceFirst("^\\.", "").toUpperCase(Locale.ROOT);
	}

	private boolean matchesFilters(
			Submission submission,
			Long assignmentId,
			Long documentRequirementId,
			SubmissionStatus status,
			Long submittedById,
			Long projectId,
			Long courseClassId) {
		return matches(submission.getAssignmentId(), assignmentId)
				&& matches(submission.getRequirementId(), documentRequirementId)
				&& (status == null || submission.getStatus() == status)
				&& matches(submission.getSubmittedBy().getId(), submittedById)
				&& matches(submission.getProjectId(), projectId)
				&& matches(submission.getCourseClassId(), courseClassId);
	}

	private boolean matches(Long actual, Long expected) {
		return expected == null || expected.equals(actual);
	}

	private boolean hasValidUploadedFile(Submission submission) {
		return fileRepository
				.findBySubmissionIdAndFileStatusInOrderByCreatedAtAsc(
						submission.getId(),
						Set.of(SubmissionFileStatus.UPLOADED))
				.stream()
				.anyMatch(file -> hasText(file.getOriginalFileName())
						&& (hasText(file.getStoragePath()) || hasText(file.getStoredFileName()) || hasText(file.getFileUrl())));
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private void checkCanUpdateDraft(User currentUser, Submission submission) {
		if (submission.getStatus() == SubmissionStatus.ARCHIVED) {
			throw new BadRequestException("Archived submissions are read-only");
		}
		if (submission.getStatus() != SubmissionStatus.DRAFT) {
			throw new BadRequestException("Only draft submissions can be updated");
		}
		if (isOwner(currentUser, submission) || canManageAssignment(currentUser, findAssignment(submission.getAssignmentId()))) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanArchive(User currentUser, Submission submission) {
		if (canManageAssignment(currentUser, findAssignment(submission.getAssignmentId()))) {
			return;
		}
		if (isOwner(currentUser, submission) && submission.getStatus() == SubmissionStatus.DRAFT) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanViewSubmission(User currentUser, Submission submission) {
		if (!canViewSubmission(currentUser, submission)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	private boolean canViewSubmission(User currentUser, Submission submission) {
		return isOwner(currentUser, submission)
				|| canManageAssignment(currentUser, findAssignment(submission.getAssignmentId()))
				|| isProjectOwner(currentUser, submission);
	}

	private boolean canViewProjectSubmissions(User currentUser, Long projectId) {
		return currentUser.getRole() == Role.ADMIN
				|| currentUser.getRole() == Role.INSTRUCTOR
				|| submissionRepository.findByTypeAndProjectIdOrderByCreatedAtDesc(SubmissionType.DOCUMENT, projectId)
						.stream()
						.anyMatch(submission -> isProjectOwner(currentUser, submission));
	}

	private void checkOwner(User currentUser, Submission submission) {
		if (!isOwner(currentUser, submission)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	private boolean isOwner(User currentUser, Submission submission) {
		return submission.getSubmittedBy() != null
				&& currentUser.getId().equals(submission.getSubmittedBy().getId());
	}

	private boolean isProjectOwner(User currentUser, Submission submission) {
		DocumentRequirementSetAssignment assignment = findAssignment(submission.getAssignmentId());
		return assignment.getProject() != null
				&& currentUser.getId().equals(assignment.getProject().getOwnerUserId());
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

	private Submission findSubmission(Long id) {
		return submissionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document submission not found"));
	}

	private DocumentRequirementSetAssignment findAssignment(Long id) {
		return assignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Requirement set assignment not found"));
	}

	private DocumentRequirement findRequirement(Long id) {
		return requirementRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document requirement not found"));
	}

	private CourseClass findCourseClass(Long id) {
		return courseClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course class not found"));
	}

	private User findUserByEmail(String email) {
		return identityQueryService.getUserByEmail(email);
	}

	private record SubmissionTarget(
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement
	) {
	}
}
