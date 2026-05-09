package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentEvaluationResultSummaryResponse;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CompleteDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.PublishDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReturnDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.StartDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UnpublishDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateCriterionScoreRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.EvaluationPublicationStatusResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentEvaluationResultResponse;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentEvaluationFindingType;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentEvaluationStatus;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RubricScoringType;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluation;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluationCriterionScore;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluationFinding;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.model.SubmissionFile;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.EvaluationRubric;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricCriterion;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricLevel;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentEvaluationCriterionScoreRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentEvaluationFindingRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentEvaluationRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.submission.service.SubmissionQueryService;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.RubricCriterionRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.RubricLevelRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class DocumentEvaluationServiceImplTest {

	@Mock
	private DocumentEvaluationRepository evaluationRepository;

	@Mock
	private DocumentEvaluationCriterionScoreRepository criterionScoreRepository;

	@Mock
	private DocumentEvaluationFindingRepository findingRepository;

	@Mock
	private DocumentRequirementSetAssignmentRepository assignmentRepository;

	@Mock
	private SubmissionQueryService submissionQueryService;

	@Mock
	private RubricCriterionRepository criterionRepository;

	@Mock
	private RubricLevelRepository levelRepository;

	@Mock
	private UserRepository userRepository;

	private DocumentEvaluationServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new DocumentEvaluationServiceImpl(
				evaluationRepository,
				criterionScoreRepository,
				findingRepository,
				assignmentRepository,
				submissionQueryService,
				criterionRepository,
				levelRepository,
				userRepository);
	}

	@Test
	void shouldStartEvaluationAndGenerateCriterionScoresFromRubric() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		EvaluationRubric rubric = rubric(30L);
		RubricCriterion criterionOne = criterion(31L, rubric, "Content", "10.00", 1);
		RubricCriterion criterionTwo = criterion(32L, rubric, "Clarity", "5.00", 2);
		rubric.addCriterion(criterionOne);
		rubric.addCriterion(criterionTwo);
		requirement.setRubric(rubric);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		Submission submission = submission(50L, student, assignment, requirement, SubmissionStatus.SUBMITTED);
		stubStartEvaluation(instructor, submission, List.of(uploadedFile(60L, submission)), false);
		when(evaluationRepository.save(any(DocumentEvaluation.class))).thenAnswer(invocation -> {
			DocumentEvaluation evaluation = invocation.getArgument(0);
			evaluation.setId(70L);
			evaluation.getCriterionScores().get(0).setId(71L);
			evaluation.getCriterionScores().get(1).setId(72L);
			return evaluation;
		});

		DocumentEvaluationResponse response = service.startEvaluation(
				"instructor@example.com",
				new StartDocumentEvaluationRequest(50L));

		assertThat(response.id()).isEqualTo(70L);
		assertThat(response.status()).isEqualTo("DRAFT");
		assertThat(response.maxScore()).isEqualByComparingTo("15.00");
		assertThat(response.criterionScores()).hasSize(2);
		assertThat(response.criterionScores().get(0).criterionName()).isEqualTo("Content");
	}

	@Test
	void shouldRejectStartingEvaluationForDraftSubmission() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		Submission submission = submission(50L, student, assignment, requirement, SubmissionStatus.DRAFT);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(submissionQueryService.getSubmissionEntityById(50L)).thenReturn(submission);

		assertThatThrownBy(() -> service.startEvaluation(
				"instructor@example.com",
				new StartDocumentEvaluationRequest(50L)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only submitted, resubmitted, or accepted submissions can be evaluated");

		verify(evaluationRepository, never()).save(any(DocumentEvaluation.class));
	}

	@Test
	void shouldRejectStartingEvaluationForNonDocumentSubmission() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		Submission submission = submission(50L, student, assignment, requirement, SubmissionStatus.SUBMITTED);
		submission.setType(SubmissionType.PROJECT_PROPOSAL);
		stubStartEvaluation(instructor, submission, List.of(uploadedFile(60L, submission)), false);

		assertThatThrownBy(() -> service.startEvaluation(
				"instructor@example.com",
				new StartDocumentEvaluationRequest(50L)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only document submissions can be evaluated");

		verify(evaluationRepository, never()).save(any(DocumentEvaluation.class));
	}

	@Test
	void shouldRejectStartingEvaluationForArchivedSubmission() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		Submission submission = submission(50L, student, assignment, requirement, SubmissionStatus.ARCHIVED);
		stubStartEvaluation(instructor, submission, List.of(uploadedFile(60L, submission)), false);

		assertThatThrownBy(() -> service.startEvaluation(
				"instructor@example.com",
				new StartDocumentEvaluationRequest(50L)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only submitted, resubmitted, or accepted submissions can be evaluated");

		verify(evaluationRepository, never()).save(any(DocumentEvaluation.class));
	}

	@Test
	void shouldRejectStartingEvaluationForInactiveAssignment() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		assignment.setStatus(RequirementSetAssignmentStatus.INACTIVE);
		Submission submission = submission(50L, student, assignment, requirement, SubmissionStatus.SUBMITTED);
		stubStartEvaluation(instructor, submission, List.of(uploadedFile(60L, submission)), false);

		assertThatThrownBy(() -> service.startEvaluation(
				"instructor@example.com",
				new StartDocumentEvaluationRequest(50L)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only active requirement set assignments can be evaluated");

		verify(evaluationRepository, never()).save(any(DocumentEvaluation.class));
	}

	@Test
	void shouldRejectStartingEvaluationForInactiveRequirementSet() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		requirementSet.setStatus(ConfigurationStatus.ARCHIVED);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		Submission submission = submission(50L, student, assignment, requirement, SubmissionStatus.SUBMITTED);
		stubStartEvaluation(instructor, submission, List.of(uploadedFile(60L, submission)), false);

		assertThatThrownBy(() -> service.startEvaluation(
				"instructor@example.com",
				new StartDocumentEvaluationRequest(50L)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only active requirement sets can be evaluated");

		verify(evaluationRepository, never()).save(any(DocumentEvaluation.class));
	}

	@Test
	void shouldRejectStartingEvaluationForRequirementOutsideAssignedSet() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet assignedSet = requirementSet(10L, instructor);
		DocumentRequirementSet otherSet = requirementSet(11L, instructor);
		DocumentRequirement otherRequirement = requirement(20L, otherSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, assignedSet);
		Submission submission = submission(50L, student, assignment, otherRequirement, SubmissionStatus.SUBMITTED);
		stubStartEvaluation(instructor, submission, List.of(uploadedFile(60L, submission)), false);

		assertThatThrownBy(() -> service.startEvaluation(
				"instructor@example.com",
				new StartDocumentEvaluationRequest(50L)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Document requirement does not belong to the assigned requirement set");

		verify(evaluationRepository, never()).save(any(DocumentEvaluation.class));
	}

	@Test
	void shouldRejectStartingEvaluationWithoutUploadedFile() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		Submission submission = submission(50L, student, assignment, requirement, SubmissionStatus.SUBMITTED);
		stubStartEvaluation(instructor, submission, List.of(), false);

		assertThatThrownBy(() -> service.startEvaluation(
				"instructor@example.com",
				new StartDocumentEvaluationRequest(50L)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Submission must have at least one uploaded file before evaluation can start");
	}

	@Test
	void shouldRejectDuplicateActiveEvaluationForSubmission() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		Submission submission = submission(50L, student, assignment, requirement, SubmissionStatus.SUBMITTED);
		stubStartEvaluation(instructor, submission, List.of(uploadedFile(60L, submission)), true);

		assertThatThrownBy(() -> service.startEvaluation(
				"instructor@example.com",
				new StartDocumentEvaluationRequest(50L)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("An active evaluation already exists for this submission");
	}

	@Test
	void shouldUpdateCriterionScoreAndRecalculateTotals() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		RubricCriterion criterion = criterion(31L, rubric(30L), "Content", "10.00", 1);
		DocumentEvaluationCriterionScore criterionScore = criterionScore(71L, evaluation, criterion, "10.00");
		evaluation.addCriterionScore(criterionScore);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));
		when(criterionScoreRepository.findById(71L)).thenReturn(Optional.of(criterionScore));
		when(criterionRepository.findById(31L)).thenReturn(Optional.of(criterion));
		when(evaluationRepository.save(any(DocumentEvaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

		DocumentEvaluationResponse response = service.updateCriterionScore(
				"instructor@example.com",
				70L,
				71L,
				new UpdateCriterionScoreRequest(31L, null, new BigDecimal("8.00"), "Good coverage", null));

		assertThat(response.status()).isEqualTo("IN_PROGRESS");
		assertThat(response.totalScore()).isEqualByComparingTo("8.00");
		assertThat(response.maxScore()).isEqualByComparingTo("10.00");
		assertThat(response.percentageScore()).isEqualByComparingTo("80.00");
	}

	@Test
	void shouldRejectScoreGreaterThanMaxScore() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		RubricCriterion criterion = criterion(31L, rubric(30L), "Content", "10.00", 1);
		DocumentEvaluationCriterionScore criterionScore = criterionScore(71L, evaluation, criterion, "10.00");
		evaluation.addCriterionScore(criterionScore);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));
		when(criterionScoreRepository.findById(71L)).thenReturn(Optional.of(criterionScore));
		when(criterionRepository.findById(31L)).thenReturn(Optional.of(criterion));

		assertThatThrownBy(() -> service.updateCriterionScore(
				"instructor@example.com",
				70L,
				71L,
				new UpdateCriterionScoreRequest(31L, null, new BigDecimal("11.00"), null, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Score must not exceed the criterion maximum score");
	}

	@Test
	void shouldRejectSelectedLevelFromAnotherCriterion() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		EvaluationRubric rubric = rubric(30L);
		RubricCriterion criterion = criterion(31L, rubric, "Content", "10.00", 1);
		RubricCriterion otherCriterion = criterion(32L, rubric, "Clarity", "10.00", 2);
		RubricLevel otherLevel = level(90L, otherCriterion, "Excellent", "8.00", 1);
		DocumentEvaluationCriterionScore criterionScore = criterionScore(71L, evaluation, criterion, "10.00");
		evaluation.addCriterionScore(criterionScore);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));
		when(criterionScoreRepository.findById(71L)).thenReturn(Optional.of(criterionScore));
		when(criterionRepository.findById(31L)).thenReturn(Optional.of(criterion));
		when(levelRepository.findById(90L)).thenReturn(Optional.of(otherLevel));

		assertThatThrownBy(() -> service.updateCriterionScore(
				"instructor@example.com",
				70L,
				71L,
				new UpdateCriterionScoreRequest(31L, 90L, null, null, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Selected rubric level does not belong to the criterion");
	}

	@Test
	void shouldRejectEditingCompletedReturnedAndArchivedEvaluations() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);

		for (DocumentEvaluationStatus status : List.of(
				DocumentEvaluationStatus.COMPLETED,
				DocumentEvaluationStatus.RETURNED,
				DocumentEvaluationStatus.ARCHIVED)) {
			DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
			evaluation.setStatus(status);
			when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
			when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));

			assertThatThrownBy(() -> service.updateFeedback(
					"instructor@example.com",
					70L,
					new com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentEvaluationFeedbackRequest(
							"Feedback",
							"Remarks")))
					.isInstanceOf(BadRequestException.class)
					.hasMessage("Completed, returned, and archived evaluations are read-only");
		}
	}

	@Test
	void shouldCompleteEvaluation() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		DocumentEvaluationCriterionScore criterionScore = criterionScore(
				71L,
				evaluation,
				criterion(31L, rubric(30L), "Content", "10.00", 1),
				"10.00");
		criterionScore.setScore(new BigDecimal("9.00"));
		evaluation.addCriterionScore(criterionScore);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));
		when(evaluationRepository.save(any(DocumentEvaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

		DocumentEvaluationResponse response = service.completeEvaluation(
				"instructor@example.com",
				70L,
				new CompleteDocumentEvaluationRequest("Accepted", "Ready"));

		assertThat(response.status()).isEqualTo("COMPLETED");
		assertThat(response.finalizedAt()).isNotNull();
		assertThat(response.totalScore()).isEqualByComparingTo("9.00");
	}

	@Test
	void shouldReturnEvaluationAndUpdateSubmissionStatus() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));
		when(evaluationRepository.save(any(DocumentEvaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

		DocumentEvaluationResponse response = service.returnEvaluation(
				"instructor@example.com",
				70L,
				new ReturnDocumentEvaluationRequest("Needs revision", "Missing detail", "Revise sections"));

		assertThat(response.status()).isEqualTo("RETURNED");
		assertThat(response.returnedAt()).isNotNull();
	}

	@Test
	void shouldPublishCompletedEvaluation() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		evaluation.setStatus(DocumentEvaluationStatus.COMPLETED);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));
		when(evaluationRepository.save(any(DocumentEvaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

		EvaluationPublicationStatusResponse response = service.publishEvaluation(
				"instructor@example.com",
				70L,
				new PublishDocumentEvaluationRequest("Released"));

		assertThat(response.published()).isTrue();
		assertThat(response.publishedAt()).isNotNull();
		assertThat(response.publishedById()).isEqualTo(90L);
		assertThat(response.publishNote()).isEqualTo("Released");
	}

	@Test
	void shouldPublishReturnedEvaluation() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		evaluation.setStatus(DocumentEvaluationStatus.RETURNED);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));
		when(evaluationRepository.save(any(DocumentEvaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

		EvaluationPublicationStatusResponse response = service.publishEvaluation(
				"instructor@example.com",
				70L,
				new PublishDocumentEvaluationRequest("Revision feedback released"));

		assertThat(response.published()).isTrue();
		assertThat(response.evaluationStatus()).isEqualTo("RETURNED");
	}

	@Test
	void shouldRejectPublishingDraftEvaluation() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));

		assertThatThrownBy(() -> service.publishEvaluation(
				"instructor@example.com",
				70L,
				new PublishDocumentEvaluationRequest("Released")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only completed or returned evaluations can be published");
	}

	@Test
	void shouldRejectPublishingArchivedEvaluation() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		evaluation.setStatus(DocumentEvaluationStatus.ARCHIVED);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));

		assertThatThrownBy(() -> service.publishEvaluation(
				"instructor@example.com",
				70L,
				new PublishDocumentEvaluationRequest("Released")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Archived evaluations cannot be published");
	}

	@Test
	void shouldReturnPublishedEvaluationResultForOwningStudent() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		evaluation.setStatus(DocumentEvaluationStatus.COMPLETED);
		evaluation.setPublished(true);
		evaluation.setPublishedAt(LocalDateTime.now());
		evaluation.setPublishNote("Released");
		DocumentEvaluationCriterionScore criterionScore = criterionScore(
				71L,
				evaluation,
				criterion(31L, rubric(30L), "Content", "10.00", 1),
				"10.00");
		criterionScore.setScore(new BigDecimal("9.00"));
		criterionScore.setComment("Strong work");
		evaluation.addCriterionScore(criterionScore);
		evaluation.addFinding(finding(81L, evaluation));
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(evaluationRepository.findBySubmissionIdAndSubmittedByIdAndPublishedTrueAndStatusIn(
				50L,
				1L,
				List.of(DocumentEvaluationStatus.COMPLETED, DocumentEvaluationStatus.RETURNED)))
				.thenReturn(Optional.of(evaluation));

		StudentEvaluationResultResponse response = service.getMySubmissionResult("student@example.com", 50L);

		assertThat(response.evaluationId()).isEqualTo(70L);
		assertThat(response.published()).isTrue();
		assertThat(response.criterionScores()).hasSize(1);
		assertThat(response.findings()).hasSize(1);
		assertThat(response.criterionScores().get(0).criterionName()).isEqualTo("Content");
	}

	@Test
	void shouldNotReturnUnpublishedResultToStudent() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(evaluationRepository.findBySubmissionIdAndSubmittedByIdAndPublishedTrueAndStatusIn(
				50L,
				1L,
				List.of(DocumentEvaluationStatus.COMPLETED, DocumentEvaluationStatus.RETURNED)))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getMySubmissionResult("student@example.com", 50L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Published evaluation result not found");
	}

	@Test
	void shouldListOnlyPublishedResultsForCurrentStudent() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		evaluation.setStatus(DocumentEvaluationStatus.COMPLETED);
		evaluation.setPublished(true);
		evaluation.setPublishedAt(LocalDateTime.now());
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(evaluationRepository.findBySubmittedByIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
				1L,
				List.of(DocumentEvaluationStatus.COMPLETED, DocumentEvaluationStatus.RETURNED)))
				.thenReturn(List.of(evaluation));

		List<StudentEvaluationResultSummaryResponse> response = service.getMyPublishedResults("student@example.com");

		assertThat(response).hasSize(1);
		assertThat(response.get(0).evaluationId()).isEqualTo(70L);
		assertThat(response.get(0).published()).isTrue();
	}

	@Test
	void shouldLimitLegacySubmittedEvaluationListToPublishedResultsForStudent() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		evaluation.setStatus(DocumentEvaluationStatus.COMPLETED);
		evaluation.setPublished(true);
		evaluation.setPublishedAt(LocalDateTime.now());
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(evaluationRepository.findBySubmittedByIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
				1L,
				List.of(DocumentEvaluationStatus.COMPLETED, DocumentEvaluationStatus.RETURNED)))
				.thenReturn(List.of(evaluation));

		List<?> response = service.getMySubmittedEvaluations("student@example.com");

		assertThat(response).hasSize(1);
		verify(evaluationRepository, never()).findBySubmittedByIdOrderByCreatedAtDesc(1L);
	}

	@Test
	void shouldUnpublishEvaluationAndHideResultFromStudentQuery() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		evaluation.setStatus(DocumentEvaluationStatus.COMPLETED);
		evaluation.setPublished(true);
		evaluation.setPublishedAt(LocalDateTime.now());
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));
		when(evaluationRepository.save(any(DocumentEvaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

		EvaluationPublicationStatusResponse response = service.unpublishEvaluation(
				"instructor@example.com",
				70L,
				new UnpublishDocumentEvaluationRequest("Correction needed"));

		assertThat(response.published()).isFalse();
		assertThat(response.unpublishedAt()).isNotNull();
		assertThat(response.unpublishedById()).isEqualTo(90L);
		assertThat(response.unpublishReason()).isEqualTo("Correction needed");
	}

	@Test
	void shouldRejectStudentPublishingEvaluation() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		evaluation.setStatus(DocumentEvaluationStatus.COMPLETED);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));

		assertThatThrownBy(() -> service.publishEvaluation(
				"student@example.com",
				70L,
				new PublishDocumentEvaluationRequest("Released")))
				.isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void shouldRejectStudentUpdatingEvaluation() {
		User instructor = user(90L, "instructor@example.com", Role.INSTRUCTOR);
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, instructor);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(40L, requirementSet);
		DocumentEvaluation evaluation = evaluation(70L, instructor, student, assignment, requirement);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(evaluationRepository.findById(70L)).thenReturn(Optional.of(evaluation));

		assertThatThrownBy(() -> service.updateCriterionScore(
				"student@example.com",
				70L,
				71L,
				new UpdateCriterionScoreRequest(31L, null, new BigDecimal("8.00"), null, null)))
				.isInstanceOf(AccessDeniedException.class);
	}

	private void stubStartEvaluation(
			User instructor,
			Submission submission,
			List<SubmissionFile> files,
			boolean duplicateExists) {
		when(userRepository.findByEmail(instructor.getEmail())).thenReturn(Optional.of(instructor));
		when(submissionQueryService.getSubmissionEntityById(submission.getId())).thenReturn(submission);
		files.forEach(submission::addFile);
		if (files.isEmpty()) {
			return;
		}
		if (duplicateExists) {
			DocumentRequirementSetAssignment assignment = assignmentRepository.findById(submission.getAssignmentId())
					.orElseThrow();
			DocumentRequirement requirement = assignment.getRequirementSet().getDocumentRequirements().get(0);
			org.mockito.Mockito.lenient().when(evaluationRepository.findBySubmissionIdAndStatusNot(
					submission.getId(),
					DocumentEvaluationStatus.ARCHIVED))
					.thenReturn(Optional.of(evaluation(80L, instructor, submission.getSubmittedBy(),
							assignment,
							requirement)));
		}
		else {
			org.mockito.Mockito.lenient().when(evaluationRepository.findBySubmissionIdAndStatusNot(
					submission.getId(),
					DocumentEvaluationStatus.ARCHIVED)).thenReturn(Optional.empty());
		}
	}

	private User user(Long id, String email, Role role) {
		User user = new User("Test", null, "User", email, "encoded-password", role);
		user.setId(id);
		return user;
	}

	private DocumentRequirementSet requirementSet(Long id, User ownerInstructor) {
		DocumentRequirementSet requirementSet = new DocumentRequirementSet("Requirement Set", ConfigurationStatus.ACTIVE);
		requirementSet.setId(id);
		requirementSet.setOwnerInstructor(ownerInstructor);
		return requirementSet;
	}

	private DocumentRequirement requirement(Long id, DocumentRequirementSet requirementSet) {
		DocumentRequirement requirement = new DocumentRequirement("Project Document", 1);
		requirement.setId(id);
		requirementSet.addDocumentRequirement(requirement);
		return requirement;
	}

	private DocumentRequirementSetAssignment classAssignment(Long id, DocumentRequirementSet requirementSet) {
		DocumentRequirementSetAssignment assignment = new DocumentRequirementSetAssignment(
				requirementSet,
				RequirementSetAssignmentType.COURSE_CLASS,
				RequirementSetAssignmentStatus.ACTIVE,
				LocalDateTime.now());
		assignment.setId(id);
		CourseClass courseClass = new CourseClass("Software Engineering", "SE201");
		courseClass.setId(100L);
		assignment.setCourseClass(courseClass);
		return assignment;
	}

	private Submission submission(
			Long id,
			User student,
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement,
			SubmissionStatus status) {
		Submission submission = new Submission();
		submission.setType(SubmissionType.DOCUMENT);
		submission.setId(id);
		submission.setSubmittedBy(student);
		submission.setAssignmentId(assignment.getId());
		submission.setRequirementId(requirement.getId());
		submission.setCourseClassId(assignment.getCourseClass() == null ? null : assignment.getCourseClass().getId());
		submission.setStatus(status);
		submission.setSubmissionTitle("Project Document");
		submission.setAttemptNumber(1);
		submission.setSubmittedAt(LocalDateTime.now());
		submission.setLastUpdatedAt(LocalDateTime.now());
		org.mockito.Mockito.lenient().when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
		return submission;
	}

	private SubmissionFile uploadedFile(Long id, Submission submission) {
		SubmissionFile file = new SubmissionFile();
		file.setId(id);
		file.setSubmission(submission);
		file.setOriginalFileName("document.pdf");
		file.setStoragePath("uploads/document.pdf");
		file.setFileStatus(SubmissionFileStatus.UPLOADED);
		return file;
	}

	private EvaluationRubric rubric(Long id) {
		EvaluationRubric rubric = new EvaluationRubric(
				"Rubric",
				new BigDecimal("10.00"),
				RubricScoringType.POINTS,
				ConfigurationStatus.ACTIVE);
		rubric.setId(id);
		return rubric;
	}

	private RubricCriterion criterion(
			Long id,
			EvaluationRubric rubric,
			String name,
			String maxPoints,
			Integer sortOrder) {
		RubricCriterion criterion = new RubricCriterion(name, new BigDecimal(maxPoints), sortOrder);
		criterion.setId(id);
		criterion.setRubric(rubric);
		return criterion;
	}

	private RubricLevel level(
			Long id,
			RubricCriterion criterion,
			String levelName,
			String points,
			Integer sortOrder) {
		RubricLevel level = new RubricLevel(levelName, new BigDecimal(points), sortOrder);
		level.setId(id);
		level.setCriterion(criterion);
		return level;
	}

	private DocumentEvaluation evaluation(
			Long id,
			User instructor,
			User student,
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement) {
		DocumentEvaluation evaluation = new DocumentEvaluation();
		evaluation.setId(id);
		evaluation.setSubmissionId(50L);
		evaluation.setAssignment(assignment);
		evaluation.setRequirementSet(assignment.getRequirementSet());
		evaluation.setDocumentRequirement(requirement);
		evaluation.setCourseClass(assignment.getCourseClass());
		evaluation.setEvaluatedBy(instructor);
		evaluation.setSubmittedBy(student);
		evaluation.setStatus(DocumentEvaluationStatus.DRAFT);
		evaluation.setStartedAt(LocalDateTime.now());
		return evaluation;
	}

	private DocumentEvaluationCriterionScore criterionScore(
			Long id,
			DocumentEvaluation evaluation,
			RubricCriterion criterion,
			String maxScore) {
		DocumentEvaluationCriterionScore criterionScore = new DocumentEvaluationCriterionScore();
		criterionScore.setId(id);
		criterionScore.setEvaluation(evaluation);
		criterionScore.setCriterion(criterion);
		criterionScore.setMaxScore(new BigDecimal(maxScore));
		criterionScore.setDisplayOrder(criterion.getSortOrder());
		return criterionScore;
	}

	private DocumentEvaluationFinding finding(Long id, DocumentEvaluation evaluation) {
		DocumentEvaluationFinding finding = new DocumentEvaluationFinding();
		finding.setId(id);
		finding.setEvaluation(evaluation);
		finding.setType(DocumentEvaluationFindingType.REVISION_REQUIRED);
		finding.setTitle("Revise content");
		finding.setDescription("Add supporting details");
		finding.setRecommendation("Revise the affected section");
		finding.setSeverity(3);
		finding.setDisplayOrder(1);
		return finding;
	}
}
