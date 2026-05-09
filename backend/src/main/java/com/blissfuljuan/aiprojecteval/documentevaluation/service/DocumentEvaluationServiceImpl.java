package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AddDocumentEvaluationFindingRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.BulkUpdateCriterionScoresRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CompleteDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.PublishDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReturnDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.StartDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UnpublishDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateCriterionScoreRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentEvaluationFeedbackRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentEvaluationFindingRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationFindingResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.EvaluationPublicationStatusResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentEvaluationResultResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentEvaluationResultSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentEvaluationFindingType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentEvaluationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.mapper.DocumentEvaluationMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluation;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluationCriterionScore;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluationFinding;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.EvaluationRubric;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricCriterion;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricLevel;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentEvaluationCriterionScoreRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentEvaluationFindingRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentEvaluationRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.RubricCriterionRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.RubricLevelRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.model.SubmissionFile;
import com.blissfuljuan.aiprojecteval.submission.service.SubmissionQueryService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentEvaluationServiceImpl implements DocumentEvaluationService {

	private static final Set<SubmissionStatus> EVALUATABLE_SUBMISSION_STATUSES = Set.of(
			SubmissionStatus.SUBMITTED,
			SubmissionStatus.RESUBMITTED,
			SubmissionStatus.ACCEPTED);
	private static final Set<DocumentEvaluationStatus> EDITABLE_STATUSES = Set.of(
			DocumentEvaluationStatus.DRAFT,
			DocumentEvaluationStatus.IN_PROGRESS);
	private static final List<DocumentEvaluationStatus> RELEASED_RESULT_STATUSES = List.of(
			DocumentEvaluationStatus.COMPLETED,
			DocumentEvaluationStatus.RETURNED);

	private final DocumentEvaluationRepository evaluationRepository;
	private final DocumentEvaluationCriterionScoreRepository criterionScoreRepository;
	private final DocumentEvaluationFindingRepository findingRepository;
	private final DocumentRequirementSetAssignmentRepository assignmentRepository;
	private final SubmissionQueryService submissionQueryService;
	private final RubricCriterionRepository criterionRepository;
	private final RubricLevelRepository levelRepository;
	private final UserRepository userRepository;

	DocumentEvaluationServiceImpl(
			DocumentEvaluationRepository evaluationRepository,
			DocumentEvaluationCriterionScoreRepository criterionScoreRepository,
			DocumentEvaluationFindingRepository findingRepository,
			DocumentRequirementSetAssignmentRepository assignmentRepository,
			SubmissionQueryService submissionQueryService,
			RubricCriterionRepository criterionRepository,
			RubricLevelRepository levelRepository,
			UserRepository userRepository) {
		this.evaluationRepository = evaluationRepository;
		this.criterionScoreRepository = criterionScoreRepository;
		this.findingRepository = findingRepository;
		this.assignmentRepository = assignmentRepository;
		this.submissionQueryService = submissionQueryService;
		this.criterionRepository = criterionRepository;
		this.levelRepository = levelRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public DocumentEvaluationResponse startEvaluation(String currentUserEmail, StartDocumentEvaluationRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		Submission submission = findSubmission(request.submissionId());
		checkCanEvaluateSubmission(currentUser, submission);
		validateSubmissionCanBeEvaluated(submission);

		evaluationRepository.findBySubmissionIdAndStatusNot(
						submission.getId(),
						DocumentEvaluationStatus.ARCHIVED)
				.ifPresent(existing -> {
					throw new BadRequestException("An active evaluation already exists for this submission");
				});

		DocumentEvaluation evaluation = new DocumentEvaluation();
		DocumentRequirementSetAssignment assignment = findAssignment(submission.getAssignmentId());
		DocumentRequirement requirement = assignment.getRequirementSet().getDocumentRequirements()
				.stream()
				.filter(item -> item.getId().equals(submission.getRequirementId()))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Document requirement not found"));

		evaluation.setSubmissionId(submission.getId());
		evaluation.setAssignment(assignment);
		evaluation.setDocumentRequirement(requirement);
		evaluation.setRequirementSet(assignment.getRequirementSet());
		evaluation.setProject(assignment.getProject());
		evaluation.setCourseClass(assignment.getCourseClass());
		evaluation.setEvaluatedBy(currentUser);
		evaluation.setSubmittedBy(submission.getSubmittedBy());
		evaluation.setStatus(DocumentEvaluationStatus.DRAFT);
		evaluation.setStartedAt(LocalDateTime.now());

		EvaluationRubric rubric = requirement.getRubric();
		if (rubric != null) {
			rubric.getCriteria().stream()
					.sorted((left, right) -> left.getSortOrder().compareTo(right.getSortOrder()))
					.forEach(criterion -> evaluation.addCriterionScore(buildCriterionScore(criterion)));
		}
		recalculateScores(evaluation);

		return DocumentEvaluationMapper.toResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentEvaluationResponse getEvaluationById(String currentUserEmail, Long evaluationId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanViewEvaluation(currentUser, evaluation);

		return DocumentEvaluationMapper.toResponse(evaluation);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentEvaluationResponse getEvaluationBySubmission(String currentUserEmail, Long submissionId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = evaluationRepository.findBySubmissionIdAndStatusNot(
						submissionId,
						DocumentEvaluationStatus.ARCHIVED)
				.orElseThrow(() -> new ResourceNotFoundException("Document evaluation not found"));
		checkCanViewEvaluation(currentUser, evaluation);

		return DocumentEvaluationMapper.toResponse(evaluation);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentEvaluationSummaryResponse> getEvaluationsByAssignment(
			String currentUserEmail,
			Long assignmentId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = assignmentRepository.findById(assignmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Requirement set assignment not found"));
		checkCanManageAssignment(currentUser, assignment);
		List<DocumentEvaluation> evaluations = evaluationRepository.findByAssignmentIdOrderByCreatedAtDesc(assignmentId);

		return evaluations.stream()
				.filter(evaluation -> evaluation.getStatus() != DocumentEvaluationStatus.ARCHIVED)
				.map(DocumentEvaluationMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentEvaluationSummaryResponse> getMySubmittedEvaluations(String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		if (currentUser.getRole() == Role.STUDENT) {
			return evaluationRepository
					.findBySubmittedByIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
							currentUser.getId(),
							RELEASED_RESULT_STATUSES)
					.stream()
					.map(DocumentEvaluationMapper::toSummaryResponse)
					.toList();
		}
		return evaluationRepository.findBySubmittedByIdOrderByCreatedAtDesc(currentUser.getId())
				.stream()
				.filter(evaluation -> evaluation.getStatus() != DocumentEvaluationStatus.ARCHIVED)
				.map(DocumentEvaluationMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentEvaluationSummaryResponse> getMyAssignedEvaluations(String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		if (currentUser.getRole() == Role.STUDENT) {
			throw new AccessDeniedException("Access denied");
		}
		return evaluationRepository.findByEvaluatedByIdOrderByCreatedAtDesc(currentUser.getId())
				.stream()
				.filter(evaluation -> evaluation.getStatus() != DocumentEvaluationStatus.ARCHIVED)
				.map(DocumentEvaluationMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional
	public DocumentEvaluationResponse updateCriterionScore(
			String currentUserEmail,
			Long evaluationId,
			Long criterionScoreId,
			UpdateCriterionScoreRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanModifyEvaluation(currentUser, evaluation);
		DocumentEvaluationCriterionScore criterionScore = findCriterionScore(criterionScoreId);
		checkCriterionScoreBelongsToEvaluation(criterionScore, evaluation);

		applyCriterionScoreUpdate(criterionScore, request);
		markInProgress(evaluation);
		recalculateScores(evaluation);

		return DocumentEvaluationMapper.toResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional
	public DocumentEvaluationResponse bulkUpdateCriterionScores(
			String currentUserEmail,
			Long evaluationId,
			BulkUpdateCriterionScoresRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanModifyEvaluation(currentUser, evaluation);

		request.scores().forEach(scoreRequest -> {
			DocumentEvaluationCriterionScore criterionScore = criterionScoreRepository
					.findByEvaluationIdAndCriterionId(evaluation.getId(), scoreRequest.criterionId())
					.orElseThrow(() -> new ResourceNotFoundException("Criterion score not found"));
			applyCriterionScoreUpdate(criterionScore, scoreRequest);
		});
		markInProgress(evaluation);
		recalculateScores(evaluation);

		return DocumentEvaluationMapper.toResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional
	public DocumentEvaluationResponse addFinding(
			String currentUserEmail,
			Long evaluationId,
			AddDocumentEvaluationFindingRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanModifyEvaluation(currentUser, evaluation);

		DocumentEvaluationFinding finding = new DocumentEvaluationFinding();
		finding.setType(parseFindingType(request.type()));
		finding.setTitle(trimToNull(request.title()));
		finding.setDescription(trimToNull(request.description()));
		finding.setRecommendation(trimToNull(request.recommendation()));
		finding.setSeverity(request.severity());
		finding.setDisplayOrder(request.displayOrder() == null ? nextFindingDisplayOrder(evaluation) : request.displayOrder());
		evaluation.addFinding(finding);
		markInProgress(evaluation);

		return DocumentEvaluationMapper.toResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional
	public DocumentEvaluationResponse updateFinding(
			String currentUserEmail,
			Long evaluationId,
			Long findingId,
			UpdateDocumentEvaluationFindingRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanModifyEvaluation(currentUser, evaluation);
		DocumentEvaluationFinding finding = findFinding(findingId);
		checkFindingBelongsToEvaluation(finding, evaluation);

		finding.setType(parseFindingType(request.type()));
		finding.setTitle(trimToNull(request.title()));
		finding.setDescription(trimToNull(request.description()));
		finding.setRecommendation(trimToNull(request.recommendation()));
		finding.setSeverity(request.severity());
		finding.setDisplayOrder(request.displayOrder() == null ? finding.getDisplayOrder() : request.displayOrder());
		markInProgress(evaluation);

		return DocumentEvaluationMapper.toResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional
	public DocumentEvaluationFindingResponse deleteFinding(String currentUserEmail, Long evaluationId, Long findingId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanModifyEvaluation(currentUser, evaluation);
		DocumentEvaluationFinding finding = findFinding(findingId);
		checkFindingBelongsToEvaluation(finding, evaluation);
		DocumentEvaluationFindingResponse response = DocumentEvaluationMapper.toFindingResponse(finding);

		evaluation.getFindings().remove(finding);
		findingRepository.delete(finding);

		return response;
	}

	@Override
	@Transactional
	public DocumentEvaluationResponse updateFeedback(
			String currentUserEmail,
			Long evaluationId,
			UpdateDocumentEvaluationFeedbackRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanModifyEvaluation(currentUser, evaluation);

		evaluation.setGeneralFeedback(trimToNull(request.generalFeedback()));
		evaluation.setEvaluatorRemarks(trimToNull(request.evaluatorRemarks()));
		markInProgress(evaluation);

		return DocumentEvaluationMapper.toResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional
	public DocumentEvaluationResponse completeEvaluation(
			String currentUserEmail,
			Long evaluationId,
			CompleteDocumentEvaluationRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanModifyEvaluation(currentUser, evaluation);

		evaluation.setGeneralFeedback(trimToNull(request.generalFeedback()));
		evaluation.setEvaluatorRemarks(trimToNull(request.evaluatorRemarks()));
		recalculateScores(evaluation);
		evaluation.setStatus(DocumentEvaluationStatus.COMPLETED);
		evaluation.setFinalizedAt(LocalDateTime.now());

		return DocumentEvaluationMapper.toResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional
	public DocumentEvaluationResponse returnEvaluation(
			String currentUserEmail,
			Long evaluationId,
			ReturnDocumentEvaluationRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanModifyEvaluation(currentUser, evaluation);

		evaluation.setGeneralFeedback(trimToNull(request.generalFeedback()));
		evaluation.setEvaluatorRemarks(trimToNull(request.evaluatorRemarks()));
		evaluation.setRevisionReason(trimToNull(request.revisionReason()));
		recalculateScores(evaluation);
		evaluation.setStatus(DocumentEvaluationStatus.RETURNED);
		evaluation.setReturnedAt(LocalDateTime.now());
		return DocumentEvaluationMapper.toResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional
	public DocumentEvaluationResponse archiveEvaluation(String currentUserEmail, Long evaluationId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		if (!canArchiveEvaluation(currentUser, evaluation)) {
			throw new AccessDeniedException("Access denied");
		}
		evaluation.setStatus(DocumentEvaluationStatus.ARCHIVED);

		return DocumentEvaluationMapper.toResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional
	public EvaluationPublicationStatusResponse publishEvaluation(
			String currentUserEmail,
			Long evaluationId,
			PublishDocumentEvaluationRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanPublishEvaluation(currentUser, evaluation);
		if (evaluation.isPublished()) {
			throw new BadRequestException("Document evaluation is already published");
		}
		if (!RELEASED_RESULT_STATUSES.contains(evaluation.getStatus())) {
			throw new BadRequestException("Only completed or returned evaluations can be published");
		}

		evaluation.setPublished(true);
		evaluation.setPublishedAt(LocalDateTime.now());
		evaluation.setPublishedBy(currentUser);
		evaluation.setPublishNote(trimToNull(request.publishNote()));
		evaluation.setUnpublishedAt(null);
		evaluation.setUnpublishedBy(null);
		evaluation.setUnpublishReason(null);

		return DocumentEvaluationMapper.toPublicationStatusResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional
	public EvaluationPublicationStatusResponse unpublishEvaluation(
			String currentUserEmail,
			Long evaluationId,
			UnpublishDocumentEvaluationRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		checkCanUnpublishEvaluation(currentUser, evaluation);
		if (!evaluation.isPublished()) {
			throw new BadRequestException("Document evaluation is not published");
		}
		if (evaluation.getStatus() == DocumentEvaluationStatus.ARCHIVED) {
			throw new BadRequestException("Archived evaluations cannot be unpublished");
		}

		evaluation.setPublished(false);
		evaluation.setUnpublishedAt(LocalDateTime.now());
		evaluation.setUnpublishedBy(currentUser);
		evaluation.setUnpublishReason(trimToNull(request.reason()));

		return DocumentEvaluationMapper.toPublicationStatusResponse(evaluationRepository.save(evaluation));
	}

	@Override
	@Transactional(readOnly = true)
	public EvaluationPublicationStatusResponse getPublicationStatus(String currentUserEmail, Long evaluationId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = findEvaluation(evaluationId);
		if (!canManageEvaluation(currentUser, evaluation)) {
			throw new AccessDeniedException("Access denied");
		}

		return DocumentEvaluationMapper.toPublicationStatusResponse(evaluation);
	}

	@Override
	@Transactional(readOnly = true)
	public StudentEvaluationResultResponse getMySubmissionResult(String currentUserEmail, Long submissionId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentEvaluation evaluation = evaluationRepository
				.findBySubmissionIdAndSubmittedByIdAndPublishedTrueAndStatusIn(
						submissionId,
						currentUser.getId(),
						RELEASED_RESULT_STATUSES)
				.orElseThrow(() -> new ResourceNotFoundException("Published evaluation result not found"));

		return DocumentEvaluationMapper.toStudentResultResponse(evaluation);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StudentEvaluationResultSummaryResponse> getMyPublishedResults(String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		return evaluationRepository
				.findBySubmittedByIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
						currentUser.getId(),
						RELEASED_RESULT_STATUSES)
				.stream()
				.map(DocumentEvaluationMapper::toStudentResultSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<StudentEvaluationResultSummaryResponse> getMyPublishedResultsByAssignment(
			String currentUserEmail,
			Long assignmentId) {
		User currentUser = findUserByEmail(currentUserEmail);
		return evaluationRepository
				.findByAssignmentIdAndSubmittedByIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
						assignmentId,
						currentUser.getId(),
						RELEASED_RESULT_STATUSES)
				.stream()
				.map(DocumentEvaluationMapper::toStudentResultSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<StudentEvaluationResultSummaryResponse> getMyPublishedResultsByProject(
			String currentUserEmail,
			Long projectId) {
		User currentUser = findUserByEmail(currentUserEmail);
		return evaluationRepository
				.findByProjectIdAndSubmittedByIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
						projectId,
						currentUser.getId(),
						RELEASED_RESULT_STATUSES)
				.stream()
				.map(DocumentEvaluationMapper::toStudentResultSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<StudentEvaluationResultSummaryResponse> getPublishedResultsByAssignment(
			String currentUserEmail,
			Long assignmentId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = assignmentRepository.findById(assignmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Requirement set assignment not found"));
		checkCanManageAssignment(currentUser, assignment);

		return evaluationRepository
				.findByAssignmentIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
						assignmentId,
						RELEASED_RESULT_STATUSES)
				.stream()
				.map(DocumentEvaluationMapper::toStudentResultSummaryResponse)
				.toList();
	}

	private DocumentEvaluationCriterionScore buildCriterionScore(RubricCriterion criterion) {
		DocumentEvaluationCriterionScore criterionScore = new DocumentEvaluationCriterionScore();
		criterionScore.setCriterion(criterion);
		criterionScore.setMaxScore(scale(criterion.getMaxPoints()));
		criterionScore.setDisplayOrder(criterion.getSortOrder());
		return criterionScore;
	}

	private void applyCriterionScoreUpdate(
			DocumentEvaluationCriterionScore criterionScore,
			UpdateCriterionScoreRequest request) {
		if (!criterionScore.getCriterion().getId().equals(request.criterionId())) {
			throw new BadRequestException("Criterion ID does not match the criterion score");
		}

		RubricCriterion criterion = criterionRepository.findById(request.criterionId())
				.orElseThrow(() -> new ResourceNotFoundException("Rubric criterion not found"));
		if (!criterion.getId().equals(criterionScore.getCriterion().getId())) {
			throw new BadRequestException("Criterion score does not belong to the requested criterion");
		}

		BigDecimal score = request.score();
		if (request.selectedLevelId() != null) {
			RubricLevel selectedLevel = levelRepository.findById(request.selectedLevelId())
					.orElseThrow(() -> new ResourceNotFoundException("Rubric level not found"));
			if (selectedLevel.getCriterion() == null
					|| !selectedLevel.getCriterion().getId().equals(criterion.getId())) {
				throw new BadRequestException("Selected rubric level does not belong to the criterion");
			}
			criterionScore.setSelectedLevel(selectedLevel);
			score = selectedLevel.getPoints();
		}
		else {
			criterionScore.setSelectedLevel(null);
		}

		validateScore(score, criterionScore.getMaxScore());
		criterionScore.setScore(scale(score));
		criterionScore.setComment(trimToNull(request.comment()));
		criterionScore.setFinding(trimToNull(request.finding()));
	}

	private void validateSubmissionCanBeEvaluated(Submission submission) {
		if (submission.getType() != SubmissionType.DOCUMENT) {
			throw new BadRequestException("Only document submissions can be evaluated");
		}
		if (!EVALUATABLE_SUBMISSION_STATUSES.contains(submission.getStatus())) {
			throw new BadRequestException("Only submitted, resubmitted, or accepted submissions can be evaluated");
		}
		if (submission.getAssignmentId() == null || submission.getRequirementId() == null) {
			throw new BadRequestException("Submission must be linked to an assignment and document requirement");
		}
		DocumentRequirementSetAssignment assignment = findAssignment(submission.getAssignmentId());
		if (assignment.getStatus() != RequirementSetAssignmentStatus.ACTIVE) {
			throw new BadRequestException("Only active requirement set assignments can be evaluated");
		}
		DocumentRequirementSet requirementSet = assignment.getRequirementSet();
		if (requirementSet == null || requirementSet.getStatus() != ConfigurationStatus.ACTIVE) {
			throw new BadRequestException("Only active requirement sets can be evaluated");
		}
		DocumentRequirement requirement = requirementSet.getDocumentRequirements()
				.stream()
				.filter(item -> item.getId().equals(submission.getRequirementId()))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Document requirement does not belong to the assigned requirement set"));
		if (requirement.getRequirementSet() == null
				|| requirementSet == null
				|| !requirement.getRequirementSet().getId().equals(requirementSet.getId())) {
			throw new BadRequestException("Document requirement does not belong to the assigned requirement set");
		}
		if (!hasValidUploadedFile(submission)) {
			throw new BadRequestException("Submission must have at least one uploaded file before evaluation can start");
		}
	}

	private boolean hasValidUploadedFile(Submission submission) {
		return submission.getFiles()
				.stream()
				.filter(file -> file.getFileStatus() == SubmissionFileStatus.UPLOADED)
				.anyMatch(this::hasUsableFileReference);
	}

	private boolean hasUsableFileReference(SubmissionFile file) {
		return hasText(file.getOriginalFileName())
				&& (hasText(file.getStoragePath()) || hasText(file.getStoredFileName()) || hasText(file.getFileUrl()));
	}

	private void recalculateScores(DocumentEvaluation evaluation) {
		BigDecimal totalScore = BigDecimal.ZERO;
		BigDecimal maxScore = BigDecimal.ZERO;
		for (DocumentEvaluationCriterionScore criterionScore : evaluation.getCriterionScores()) {
			maxScore = maxScore.add(valueOrZero(criterionScore.getMaxScore()));
			totalScore = totalScore.add(valueOrZero(criterionScore.getScore()));
		}
		evaluation.setTotalScore(scale(totalScore));
		evaluation.setMaxScore(scale(maxScore));
		if (maxScore.compareTo(BigDecimal.ZERO) == 0) {
			evaluation.setPercentageScore(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
			return;
		}
		BigDecimal percentage = totalScore
				.multiply(BigDecimal.valueOf(100))
				.divide(maxScore, 2, RoundingMode.HALF_UP);
		evaluation.setPercentageScore(percentage);
	}

	private void validateScore(BigDecimal score, BigDecimal maxScore) {
		if (score == null) {
			return;
		}
		if (score.compareTo(BigDecimal.ZERO) < 0) {
			throw new BadRequestException("Score must not be negative");
		}
		if (maxScore != null && score.compareTo(maxScore) > 0) {
			throw new BadRequestException("Score must not exceed the criterion maximum score");
		}
	}

	private BigDecimal valueOrZero(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	private BigDecimal scale(BigDecimal value) {
		return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
	}

	private void markInProgress(DocumentEvaluation evaluation) {
		if (evaluation.getStatus() == DocumentEvaluationStatus.DRAFT) {
			evaluation.setStatus(DocumentEvaluationStatus.IN_PROGRESS);
		}
	}

	private int nextFindingDisplayOrder(DocumentEvaluation evaluation) {
		return evaluation.getFindings()
				.stream()
				.map(DocumentEvaluationFinding::getDisplayOrder)
				.filter(order -> order != null)
				.max(Integer::compareTo)
				.orElse(0) + 1;
	}

	private void checkCanModifyEvaluation(User currentUser, DocumentEvaluation evaluation) {
		if (!EDITABLE_STATUSES.contains(evaluation.getStatus())) {
			throw new BadRequestException("Completed, returned, and archived evaluations are read-only");
		}
		if (canManageEvaluation(currentUser, evaluation)) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanPublishEvaluation(User currentUser, DocumentEvaluation evaluation) {
		if (evaluation.getStatus() == DocumentEvaluationStatus.ARCHIVED) {
			throw new BadRequestException("Archived evaluations cannot be published");
		}
		if (canManageEvaluation(currentUser, evaluation)) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanUnpublishEvaluation(User currentUser, DocumentEvaluation evaluation) {
		if (currentUser.getRole() == Role.ADMIN) {
			return;
		}
		if (currentUser.getRole() == Role.INSTRUCTOR && canManageAssignment(currentUser, evaluation.getAssignment())) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanViewEvaluation(User currentUser, DocumentEvaluation evaluation) {
		if (canManageEvaluation(currentUser, evaluation)) {
			return;
		}
		if (currentUser.getRole() == Role.STUDENT
				&& (isSubmittedBy(currentUser, evaluation) || isProjectOwner(currentUser, evaluation))) {
			if (isReleasedToStudent(evaluation)) {
				return;
			}
			throw new ResourceNotFoundException("Document evaluation not found");
		}
		if (isProjectOwner(currentUser, evaluation)) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanEvaluateSubmission(User currentUser, Submission submission) {
		if (canManageAssignment(currentUser, findAssignment(submission.getAssignmentId()))) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanManageAssignment(User currentUser, DocumentRequirementSetAssignment assignment) {
		if (!canManageAssignment(currentUser, assignment)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	private boolean canManageEvaluation(User currentUser, DocumentEvaluation evaluation) {
		return canManageAssignment(currentUser, evaluation.getAssignment())
				|| isEvaluatedBy(currentUser, evaluation);
	}

	private boolean canArchiveEvaluation(User currentUser, DocumentEvaluation evaluation) {
		return currentUser.getRole() == Role.ADMIN || isEvaluatedBy(currentUser, evaluation);
	}

	private boolean canManageAssignment(User currentUser, DocumentRequirementSetAssignment assignment) {
		if (currentUser.getRole() == Role.ADMIN) {
			return true;
		}
		if (currentUser.getRole() == Role.EVALUATOR) {
			return true;
		}
		DocumentRequirementSet requirementSet = assignment.getRequirementSet();
		return currentUser.getRole() == Role.INSTRUCTOR
				&& requirementSet != null
				&& requirementSet.getOwnerInstructor() != null
				&& currentUser.getId().equals(requirementSet.getOwnerInstructor().getId());
	}

	private boolean isSubmittedBy(User currentUser, DocumentEvaluation evaluation) {
		return evaluation.getSubmittedBy() != null
				&& currentUser.getId().equals(evaluation.getSubmittedBy().getId());
	}

	private boolean isEvaluatedBy(User currentUser, DocumentEvaluation evaluation) {
		return evaluation.getEvaluatedBy() != null
				&& currentUser.getId().equals(evaluation.getEvaluatedBy().getId());
	}

	private boolean isProjectOwner(User currentUser, DocumentEvaluation evaluation) {
		return evaluation.getProject() != null
				&& currentUser.getId().equals(evaluation.getProject().getOwnerUserId());
	}

	private boolean isReleasedToStudent(DocumentEvaluation evaluation) {
		return evaluation.isPublished()
				&& RELEASED_RESULT_STATUSES.contains(evaluation.getStatus());
	}

	private void checkCriterionScoreBelongsToEvaluation(
			DocumentEvaluationCriterionScore criterionScore,
			DocumentEvaluation evaluation) {
		if (criterionScore.getEvaluation() == null
				|| !criterionScore.getEvaluation().getId().equals(evaluation.getId())) {
			throw new BadRequestException("Criterion score does not belong to the evaluation");
		}
	}

	private void checkFindingBelongsToEvaluation(DocumentEvaluationFinding finding, DocumentEvaluation evaluation) {
		if (finding.getEvaluation() == null || !finding.getEvaluation().getId().equals(evaluation.getId())) {
			throw new BadRequestException("Finding does not belong to the evaluation");
		}
	}

	private DocumentEvaluationFindingType parseFindingType(String value) {
		try {
			return DocumentEvaluationFindingType.valueOf(value.trim().toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException exception) {
			throw new BadRequestException("Invalid evaluation finding type");
		}
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private DocumentEvaluation findEvaluation(Long id) {
		return evaluationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document evaluation not found"));
	}

	private DocumentRequirementSetAssignment findAssignment(Long id) {
		return assignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Requirement set assignment not found"));
	}

	private DocumentEvaluationCriterionScore findCriterionScore(Long id) {
		return criterionScoreRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Criterion score not found"));
	}

	private DocumentEvaluationFinding findFinding(Long id) {
		return findingRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Evaluation finding not found"));
	}

	private Submission findSubmission(Long id) {
		return submissionQueryService.getSubmissionEntityById(id);
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}
}
