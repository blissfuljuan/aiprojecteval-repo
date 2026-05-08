package com.blissfuljuan.aiprojecteval.documentevaluation.mapper;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationCriterionScoreResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationFindingResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.EvaluationPublicationStatusResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentCriterionScoreResultResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentEvaluationFindingResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentEvaluationResultResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentEvaluationResultSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluation;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluationCriterionScore;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluationFinding;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmission;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricCriterion;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricLevel;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import java.util.Comparator;

public final class DocumentEvaluationMapper {

	private DocumentEvaluationMapper() {
	}

	public static DocumentEvaluationResponse toResponse(DocumentEvaluation evaluation) {
		DocumentSubmission submission = evaluation.getSubmission();
		DocumentRequirementSetAssignment assignment = evaluation.getAssignment();
		DocumentRequirementSet requirementSet = evaluation.getRequirementSet();
		DocumentRequirement requirement = evaluation.getDocumentRequirement();
		Project project = evaluation.getProject();
		CourseClass courseClass = evaluation.getCourseClass();
		User submittedBy = evaluation.getSubmittedBy();
		User evaluatedBy = evaluation.getEvaluatedBy();

		return new DocumentEvaluationResponse(
				evaluation.getId(),
				submission == null ? null : submission.getId(),
				assignment == null ? null : assignment.getId(),
				requirementSet == null ? null : requirementSet.getId(),
				requirementSet == null ? null : requirementSet.getName(),
				requirement == null ? null : requirement.getId(),
				requirement == null ? null : requirement.getName(),
				project == null ? null : project.getId(),
				project == null ? null : project.getTitle(),
				courseClass == null ? null : courseClass.getId(),
				courseClass == null ? null : courseClass.getName(),
				submittedBy == null ? null : submittedBy.getId(),
				submittedBy == null ? null : formatUserName(submittedBy),
				evaluatedBy == null ? null : evaluatedBy.getId(),
				evaluatedBy == null ? null : formatUserName(evaluatedBy),
				evaluation.getStatus() == null ? null : evaluation.getStatus().name(),
				evaluation.getTotalScore(),
				evaluation.getMaxScore(),
				evaluation.getPercentageScore(),
				evaluation.getGeneralFeedback(),
				evaluation.getEvaluatorRemarks(),
				evaluation.getStartedAt(),
				evaluation.getFinalizedAt(),
				evaluation.getReturnedAt(),
				evaluation.getCriterionScores()
						.stream()
						.sorted(Comparator.comparing(DocumentEvaluationCriterionScore::getDisplayOrder)
								.thenComparing(score -> score.getId() == null ? 0L : score.getId()))
						.map(DocumentEvaluationMapper::toCriterionScoreResponse)
						.toList(),
				evaluation.getFindings()
						.stream()
						.sorted(Comparator.comparing(DocumentEvaluationFinding::getDisplayOrder)
								.thenComparing(finding -> finding.getId() == null ? 0L : finding.getId()))
						.map(DocumentEvaluationMapper::toFindingResponse)
						.toList()
		);
	}

	public static DocumentEvaluationSummaryResponse toSummaryResponse(DocumentEvaluation evaluation) {
		DocumentSubmission submission = evaluation.getSubmission();
		DocumentRequirement requirement = evaluation.getDocumentRequirement();
		User submittedBy = evaluation.getSubmittedBy();
		User evaluatedBy = evaluation.getEvaluatedBy();

		return new DocumentEvaluationSummaryResponse(
				evaluation.getId(),
				submission == null ? null : submission.getId(),
				requirement == null ? null : requirement.getId(),
				requirement == null ? null : requirement.getName(),
				submittedBy == null ? null : submittedBy.getId(),
				submittedBy == null ? null : formatUserName(submittedBy),
				evaluatedBy == null ? null : evaluatedBy.getId(),
				evaluatedBy == null ? null : formatUserName(evaluatedBy),
				evaluation.getStatus() == null ? null : evaluation.getStatus().name(),
				evaluation.getTotalScore(),
				evaluation.getMaxScore(),
				evaluation.getPercentageScore(),
				evaluation.getStartedAt(),
				evaluation.getFinalizedAt()
		);
	}

	public static EvaluationPublicationStatusResponse toPublicationStatusResponse(DocumentEvaluation evaluation) {
		User publishedBy = evaluation.getPublishedBy();
		User unpublishedBy = evaluation.getUnpublishedBy();

		return new EvaluationPublicationStatusResponse(
				evaluation.getId(),
				evaluation.getStatus() == null ? null : evaluation.getStatus().name(),
				evaluation.isPublished(),
				evaluation.getPublishedAt(),
				publishedBy == null ? null : publishedBy.getId(),
				publishedBy == null ? null : formatUserName(publishedBy),
				evaluation.getPublishNote(),
				evaluation.getUnpublishedAt(),
				unpublishedBy == null ? null : unpublishedBy.getId(),
				unpublishedBy == null ? null : formatUserName(unpublishedBy),
				evaluation.getUnpublishReason()
		);
	}

	public static StudentEvaluationResultResponse toStudentResultResponse(DocumentEvaluation evaluation) {
		DocumentSubmission submission = evaluation.getSubmission();
		DocumentRequirementSetAssignment assignment = evaluation.getAssignment();
		DocumentRequirementSet requirementSet = evaluation.getRequirementSet();
		DocumentRequirement requirement = evaluation.getDocumentRequirement();
		Project project = evaluation.getProject();
		CourseClass courseClass = evaluation.getCourseClass();

		return new StudentEvaluationResultResponse(
				evaluation.getId(),
				submission == null ? null : submission.getId(),
				assignment == null ? null : assignment.getId(),
				requirementSet == null ? null : requirementSet.getId(),
				requirementSet == null ? null : requirementSet.getName(),
				requirement == null ? null : requirement.getId(),
				requirement == null ? null : requirement.getName(),
				project == null ? null : project.getId(),
				project == null ? null : project.getTitle(),
				courseClass == null ? null : courseClass.getId(),
				courseClass == null ? null : courseClass.getName(),
				evaluation.getStatus() == null ? null : evaluation.getStatus().name(),
				evaluation.isPublished(),
				evaluation.getPublishedAt(),
				evaluation.getPublishNote(),
				evaluation.getTotalScore(),
				evaluation.getMaxScore(),
				evaluation.getPercentageScore(),
				evaluation.getGeneralFeedback(),
				evaluation.getEvaluatorRemarks(),
				evaluation.getStartedAt(),
				evaluation.getFinalizedAt(),
				evaluation.getReturnedAt(),
				evaluation.getCriterionScores()
						.stream()
						.sorted(Comparator.comparing(DocumentEvaluationCriterionScore::getDisplayOrder)
								.thenComparing(score -> score.getId() == null ? 0L : score.getId()))
						.map(DocumentEvaluationMapper::toStudentCriterionScoreResultResponse)
						.toList(),
				evaluation.getFindings()
						.stream()
						.sorted(Comparator.comparing(DocumentEvaluationFinding::getDisplayOrder)
								.thenComparing(finding -> finding.getId() == null ? 0L : finding.getId()))
						.map(DocumentEvaluationMapper::toStudentFindingResponse)
						.toList()
		);
	}

	public static StudentEvaluationResultSummaryResponse toStudentResultSummaryResponse(DocumentEvaluation evaluation) {
		DocumentSubmission submission = evaluation.getSubmission();
		DocumentRequirement requirement = evaluation.getDocumentRequirement();

		return new StudentEvaluationResultSummaryResponse(
				evaluation.getId(),
				submission == null ? null : submission.getId(),
				requirement == null ? null : requirement.getId(),
				requirement == null ? null : requirement.getName(),
				evaluation.getStatus() == null ? null : evaluation.getStatus().name(),
				evaluation.isPublished(),
				evaluation.getPublishedAt(),
				evaluation.getTotalScore(),
				evaluation.getMaxScore(),
				evaluation.getPercentageScore(),
				evaluation.getGeneralFeedback(),
				evaluation.getFinalizedAt(),
				evaluation.getReturnedAt()
		);
	}

	public static DocumentEvaluationCriterionScoreResponse toCriterionScoreResponse(
			DocumentEvaluationCriterionScore criterionScore) {
		RubricCriterion criterion = criterionScore.getCriterion();
		RubricLevel selectedLevel = criterionScore.getSelectedLevel();

		return new DocumentEvaluationCriterionScoreResponse(
				criterionScore.getId(),
				criterion == null ? null : criterion.getId(),
				criterion == null ? null : criterion.getName(),
				criterion == null ? null : criterion.getDescription(),
				criterionScore.getDisplayOrder(),
				selectedLevel == null ? null : selectedLevel.getId(),
				selectedLevel == null ? null : selectedLevel.getLevelName(),
				selectedLevel == null ? null : selectedLevel.getDescription(),
				criterionScore.getScore(),
				criterionScore.getMaxScore(),
				criterionScore.getComment(),
				criterionScore.getFinding()
		);
	}

	public static StudentCriterionScoreResultResponse toStudentCriterionScoreResultResponse(
			DocumentEvaluationCriterionScore criterionScore) {
		RubricCriterion criterion = criterionScore.getCriterion();
		RubricLevel selectedLevel = criterionScore.getSelectedLevel();

		return new StudentCriterionScoreResultResponse(
				criterion == null ? null : criterion.getId(),
				criterion == null ? null : criterion.getName(),
				criterion == null ? null : criterion.getDescription(),
				criterionScore.getDisplayOrder(),
				selectedLevel == null ? null : selectedLevel.getId(),
				selectedLevel == null ? null : selectedLevel.getLevelName(),
				selectedLevel == null ? null : selectedLevel.getDescription(),
				criterionScore.getScore(),
				criterionScore.getMaxScore(),
				criterionScore.getComment(),
				criterionScore.getFinding()
		);
	}

	public static DocumentEvaluationFindingResponse toFindingResponse(DocumentEvaluationFinding finding) {
		return new DocumentEvaluationFindingResponse(
				finding.getId(),
				finding.getType() == null ? null : finding.getType().name(),
				finding.getTitle(),
				finding.getDescription(),
				finding.getRecommendation(),
				finding.getSeverity(),
				finding.getDisplayOrder()
		);
	}

	public static StudentEvaluationFindingResponse toStudentFindingResponse(DocumentEvaluationFinding finding) {
		return new StudentEvaluationFindingResponse(
				finding.getId(),
				finding.getType() == null ? null : finding.getType().name(),
				finding.getTitle(),
				finding.getDescription(),
				finding.getRecommendation(),
				finding.getSeverity(),
				finding.getDisplayOrder()
		);
	}

	private static String formatUserName(User user) {
		StringBuilder name = new StringBuilder(user.getFirstName());
		if (user.getMiddleName() != null && !user.getMiddleName().isBlank()) {
			name.append(' ').append(user.getMiddleName());
		}
		name.append(' ').append(user.getLastName());
		return name.toString();
	}
}
