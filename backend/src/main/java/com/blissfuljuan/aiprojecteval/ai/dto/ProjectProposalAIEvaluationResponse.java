package com.blissfuljuan.aiprojecteval.ai.dto;

import com.blissfuljuan.aiprojecteval.ai.model.AIEvaluationStatus;
import com.blissfuljuan.aiprojecteval.ai.model.AIProviderType;
import com.blissfuljuan.aiprojecteval.ai.model.ProposalAIRecommendation;
import com.blissfuljuan.aiprojecteval.ai.model.ProposalReadinessLevel;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectProposalAIEvaluationResponse(
		Long id,
		Long evaluationId,
		Long proposalId,
		Long documentVersionId,
		AIEvaluationStatus status,
		AIProviderType provider,
		String model,
		Integer overallScore,
		Integer maxScore,
		ProposalReadinessLevel readinessLevel,
		ProposalAIRecommendation recommendation,
		String summary,
		List<String> strengths,
		List<String> weaknesses,
		List<String> missingSections,
		List<String> riskNotes,
		List<String> suggestedRevisions,
		List<AICriteriaScoreResponse> criteriaScores,
		LocalDateTime createdAt,
		LocalDateTime completedAt
) {
}
