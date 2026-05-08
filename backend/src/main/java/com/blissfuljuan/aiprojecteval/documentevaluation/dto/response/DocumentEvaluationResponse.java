package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DocumentEvaluationResponse(
		Long id,
		Long submissionId,
		Long assignmentId,
		Long requirementSetId,
		String requirementSetName,
		Long documentRequirementId,
		String documentRequirementName,
		Long projectId,
		String projectName,
		Long courseClassId,
		String courseClassName,
		Long submittedById,
		String submittedByName,
		Long evaluatedById,
		String evaluatedByName,
		String status,
		BigDecimal totalScore,
		BigDecimal maxScore,
		BigDecimal percentageScore,
		String generalFeedback,
		String evaluatorRemarks,
		LocalDateTime startedAt,
		LocalDateTime finalizedAt,
		LocalDateTime returnedAt,
		List<DocumentEvaluationCriterionScoreResponse> criterionScores,
		List<DocumentEvaluationFindingResponse> findings
) {
}
