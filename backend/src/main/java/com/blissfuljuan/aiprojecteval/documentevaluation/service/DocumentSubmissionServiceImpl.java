package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentSubmissionRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.DocumentSubmissionFileMetadataRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.mapper.DocumentSubmissionMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmission;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmissionFile;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentSubmissionServiceImpl implements DocumentSubmissionService {

	private final DocumentSubmissionRepository submissionRepository;
	private final DocumentSubmissionFileRepository fileRepository;
	private final DocumentRequirementSetAssignmentRepository assignmentRepository;
	private final DocumentRequirementRepository requirementRepository;
	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;
	private final CourseClassRepository courseClassRepository;

	DocumentSubmissionServiceImpl(
			DocumentSubmissionRepository submissionRepository,
			DocumentSubmissionFileRepository fileRepository,
			DocumentRequirementSetAssignmentRepository assignmentRepository,
			DocumentRequirementRepository requirementRepository,
			UserRepository userRepository,
			ProjectRepository projectRepository,
			CourseClassRepository courseClassRepository) {
		this.submissionRepository = submissionRepository;
		this.fileRepository = fileRepository;
		this.assignmentRepository = assignmentRepository;
		this.requirementRepository = requirementRepository;
		this.userRepository = userRepository;
		this.projectRepository = projectRepository;
		this.courseClassRepository = courseClassRepository;
	}

	@Override
	@Transactional
	public DocumentSubmissionResponse createDraftSubmission(
			String currentUserEmail,
			CreateDocumentSubmissionDraftRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		SubmissionTarget target = validateSubmissionTarget(request.assignmentId(), request.documentRequirementId());
		rejectDuplicateDraft(currentUser, target.assignment(), target.requirement());

		DocumentSubmission submission = buildSubmission(
				currentUser,
				target,
				request.submissionTitle(),
				request.submissionNotes(),
				DocumentSubmissionStatus.DRAFT);

		return DocumentSubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional
	public DocumentSubmissionResponse createSubmission(
			String currentUserEmail,
			CreateDocumentSubmissionRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		SubmissionTarget target = validateSubmissionTarget(request.assignmentId(), request.documentRequirementId());
		rejectDuplicateDraft(currentUser, target.assignment(), target.requirement());

		DocumentSubmission submission = buildSubmission(
				currentUser,
				target,
				request.submissionTitle(),
				request.submissionNotes(),
				DocumentSubmissionStatus.SUBMITTED);
		submission.setSubmittedAt(LocalDateTime.now());
		addFileMetadata(submission, request.files());

		return DocumentSubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional
	public DocumentSubmissionResponse updateDraftSubmission(
			String currentUserEmail,
			Long submissionId,
			UpdateDocumentSubmissionDraftRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmission submission = findSubmission(submissionId);
		checkCanUpdateDraft(currentUser, submission);

		submission.setSubmissionTitle(request.submissionTitle());
		submission.setSubmissionNotes(request.submissionNotes());
		if (request.files() != null) {
			submission.getFiles().clear();
			addFileMetadata(submission, request.files());
		}

		return DocumentSubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional
	public DocumentSubmissionResponse submitDraftSubmission(String currentUserEmail, Long submissionId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmission submission = findSubmission(submissionId);
		checkOwner(currentUser, submission);
		if (submission.getStatus() != DocumentSubmissionStatus.DRAFT) {
			throw new BadRequestException("Only draft submissions can be submitted");
		}
		if (!hasValidUploadedFile(submission)) {
			throw new BadRequestException("Submission must have at least one uploaded file before it can be submitted");
		}

		submission.setStatus(DocumentSubmissionStatus.SUBMITTED);
		submission.setSubmittedAt(LocalDateTime.now());

		return DocumentSubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentSubmissionSummaryResponse> getMySubmissions(
			String currentUserEmail,
			Long assignmentId,
			Long documentRequirementId,
			DocumentSubmissionStatus status,
			Long projectId,
			Long courseClassId) {
		User currentUser = findUserByEmail(currentUserEmail);
		return submissionRepository.findBySubmittedByIdOrderByCreatedAtDesc(currentUser.getId())
				.stream()
				.filter(submission -> matchesFilters(
						submission,
						assignmentId,
						documentRequirementId,
						status,
						null,
						projectId,
						courseClassId))
				.map(DocumentSubmissionMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentSubmissionResponse getSubmissionById(String currentUserEmail, Long submissionId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmission submission = findSubmission(submissionId);
		checkCanViewSubmission(currentUser, submission);

		return DocumentSubmissionMapper.toResponse(submission);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentSubmissionSummaryResponse> getSubmissionsByAssignment(
			String currentUserEmail,
			Long assignmentId,
			Long documentRequirementId,
			DocumentSubmissionStatus status,
			Long submittedById,
			Long projectId,
			Long courseClassId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(assignmentId);
		checkCanManageAssignment(currentUser, assignment);

		return submissionRepository.findByAssignmentIdOrderByCreatedAtDesc(assignmentId)
				.stream()
				.filter(submission -> matchesFilters(
						submission,
						null,
						documentRequirementId,
						status,
						submittedById,
						projectId,
						courseClassId))
				.map(DocumentSubmissionMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentSubmissionSummaryResponse> getSubmissionsByProject(
			String currentUserEmail,
			Long projectId,
			Long documentRequirementId,
			DocumentSubmissionStatus status,
			Long submittedById) {
		User currentUser = findUserByEmail(currentUserEmail);
		findProject(projectId);
		if (!canViewProjectSubmissions(currentUser, projectId)) {
			throw new AccessDeniedException("Access denied");
		}

		return submissionRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
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
				.map(DocumentSubmissionMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentSubmissionSummaryResponse> getSubmissionsByClass(
			String currentUserEmail,
			Long courseClassId,
			Long documentRequirementId,
			DocumentSubmissionStatus status,
			Long submittedById) {
		User currentUser = findUserByEmail(currentUserEmail);
		findCourseClass(courseClassId);
		if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.INSTRUCTOR) {
			throw new AccessDeniedException("Access denied");
		}

		return submissionRepository.findByCourseClassIdOrderByCreatedAtDesc(courseClassId)
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
				.map(DocumentSubmissionMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional
	public DocumentSubmissionResponse archiveSubmission(String currentUserEmail, Long submissionId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmission submission = findSubmission(submissionId);
		checkCanArchive(currentUser, submission);
		if (submission.getStatus() != DocumentSubmissionStatus.ARCHIVED) {
			submission.setStatus(DocumentSubmissionStatus.ARCHIVED);
		}

		return DocumentSubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional
	public DocumentSubmissionResponse addFileMetadataToDraftSubmission(
			String currentUserEmail,
			Long submissionId,
			DocumentSubmissionFileMetadataRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmission submission = findSubmission(submissionId);
		checkCanUpdateDraft(currentUser, submission);
		submission.addFile(buildFileMetadata(submission.getDocumentRequirement(), request));

		return DocumentSubmissionMapper.toResponse(submissionRepository.save(submission));
	}

	@Override
	@Transactional
	public DocumentSubmissionFileResponse removeFileMetadataFromDraftSubmission(
			String currentUserEmail,
			Long fileId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmissionFile file = fileRepository.findById(fileId)
				.orElseThrow(() -> new ResourceNotFoundException("Submission file metadata not found"));
		checkCanUpdateDraft(currentUser, file.getSubmission());
		file.setFileStatus(DocumentSubmissionFileStatus.REMOVED);

		return DocumentSubmissionMapper.toFileResponse(fileRepository.save(file));
	}

	private DocumentSubmission buildSubmission(
			User currentUser,
			SubmissionTarget target,
			String submissionTitle,
			String submissionNotes,
			DocumentSubmissionStatus status) {
		DocumentSubmission submission = new DocumentSubmission();
		submission.setAssignment(target.assignment());
		submission.setDocumentRequirement(target.requirement());
		submission.setProject(target.assignment().getProject());
		submission.setCourseClass(target.assignment().getCourseClass());
		submission.setSubmittedBy(currentUser);
		submission.setStatus(status);
		submission.setSubmissionTitle(submissionTitle);
		submission.setSubmissionNotes(submissionNotes);
		submission.setAttemptNumber(nextAttemptNumber(currentUser, target.assignment(), target.requirement()));
		submission.setLastUpdatedAt(LocalDateTime.now());
		return submission;
	}

	private void addFileMetadata(
			DocumentSubmission submission,
			List<DocumentSubmissionFileMetadataRequest> files) {
		if (files == null) {
			return;
		}
		files.forEach(file -> submission.addFile(buildFileMetadata(submission.getDocumentRequirement(), file)));
	}

	private DocumentSubmissionFile buildFileMetadata(
			DocumentRequirement requirement,
			DocumentSubmissionFileMetadataRequest request) {
		validateFileMetadata(requirement, request);

		DocumentSubmissionFile file = new DocumentSubmissionFile();
		file.setOriginalFileName(request.originalFileName());
		file.setContentType(request.contentType());
		file.setFileSize(request.fileSize());
		file.setFileExtension(normalizeExtension(request.fileExtension(), request.originalFileName()));
		file.setFileUrl(request.fileUrl());
		file.setStoragePath(request.storagePath());
		file.setChecksum(request.checksum());
		file.setFileStatus(DocumentSubmissionFileStatus.PENDING_UPLOAD);
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
		submissionRepository.findFirstBySubmittedByIdAndAssignmentIdAndDocumentRequirementIdAndStatus(
						currentUser.getId(),
						assignment.getId(),
						requirement.getId(),
						DocumentSubmissionStatus.DRAFT)
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
			DocumentSubmissionFileMetadataRequest request) {
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
			DocumentSubmission submission,
			Long assignmentId,
			Long documentRequirementId,
			DocumentSubmissionStatus status,
			Long submittedById,
			Long projectId,
			Long courseClassId) {
		return matches(submission.getAssignment().getId(), assignmentId)
				&& matches(submission.getDocumentRequirement().getId(), documentRequirementId)
				&& (status == null || submission.getStatus() == status)
				&& matches(submission.getSubmittedBy().getId(), submittedById)
				&& matches(submission.getProject() == null ? null : submission.getProject().getId(), projectId)
				&& matches(submission.getCourseClass() == null ? null : submission.getCourseClass().getId(),
						courseClassId);
	}

	private boolean matches(Long actual, Long expected) {
		return expected == null || expected.equals(actual);
	}

	private boolean hasValidUploadedFile(DocumentSubmission submission) {
		return fileRepository
				.findBySubmissionIdAndFileStatusInOrderByCreatedAtAsc(
						submission.getId(),
						Set.of(DocumentSubmissionFileStatus.UPLOADED))
				.stream()
				.anyMatch(file -> hasText(file.getOriginalFileName())
						&& (hasText(file.getStoragePath()) || hasText(file.getStoredFileName()) || hasText(file.getFileUrl())));
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private void checkCanUpdateDraft(User currentUser, DocumentSubmission submission) {
		if (submission.getStatus() == DocumentSubmissionStatus.ARCHIVED) {
			throw new BadRequestException("Archived submissions are read-only");
		}
		if (submission.getStatus() != DocumentSubmissionStatus.DRAFT) {
			throw new BadRequestException("Only draft submissions can be updated");
		}
		if (isOwner(currentUser, submission) || canManageAssignment(currentUser, submission.getAssignment())) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanArchive(User currentUser, DocumentSubmission submission) {
		if (canManageAssignment(currentUser, submission.getAssignment())) {
			return;
		}
		if (isOwner(currentUser, submission) && submission.getStatus() == DocumentSubmissionStatus.DRAFT) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanViewSubmission(User currentUser, DocumentSubmission submission) {
		if (!canViewSubmission(currentUser, submission)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	private boolean canViewSubmission(User currentUser, DocumentSubmission submission) {
		return isOwner(currentUser, submission)
				|| canManageAssignment(currentUser, submission.getAssignment())
				|| isProjectOwner(currentUser, submission.getProject());
	}

	private boolean canViewProjectSubmissions(User currentUser, Long projectId) {
		return currentUser.getRole() == Role.ADMIN
				|| currentUser.getRole() == Role.INSTRUCTOR
				|| isProjectOwner(currentUser, findProject(projectId));
	}

	private void checkOwner(User currentUser, DocumentSubmission submission) {
		if (!isOwner(currentUser, submission)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	private boolean isOwner(User currentUser, DocumentSubmission submission) {
		return submission.getSubmittedBy() != null
				&& currentUser.getId().equals(submission.getSubmittedBy().getId());
	}

	private boolean isProjectOwner(User currentUser, Project project) {
		return project != null && currentUser.getId().equals(project.getOwnerUserId());
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

	private DocumentSubmission findSubmission(Long id) {
		return submissionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document submission not found"));
	}

	private DocumentRequirementSetAssignment findAssignment(Long id) {
		return assignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Requirement set assignment not found"));
	}

	private Project findProject(Long id) {
		return projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	private CourseClass findCourseClass(Long id) {
		return courseClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course class not found"));
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private record SubmissionTarget(
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement
	) {
	}
}
