package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record StudentEvaluationResultResponse(
		Long evaluationId,
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
		String evaluationStatus,
		boolean published,
		LocalDateTime publishedAt,
		String publishNote,
		BigDecimal totalScore,
		BigDecimal maxScore,
		BigDecimal percentageScore,
		String generalFeedback,
		String evaluatorRemarks,
		LocalDateTime startedAt,
		LocalDateTime finalizedAt,
		LocalDateTime returnedAt,
		List<StudentCriterionScoreResultResponse> criterionScores,
		List<StudentEvaluationFindingResponse> findings
) {
}
