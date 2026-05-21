package com.blissfuljuan.aiprojecteval.ai.mapper;

import com.blissfuljuan.aiprojecteval.ai.dto.AICriteriaScoreResponse;
import com.blissfuljuan.aiprojecteval.ai.dto.ProjectProposalAIEvaluationResponse;
import com.blissfuljuan.aiprojecteval.ai.model.AICriteriaScore;
import com.blissfuljuan.aiprojecteval.ai.model.AIEvaluation;
import com.blissfuljuan.aiprojecteval.ai.model.ProjectProposalAIEvaluationResult;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjectProposalAIEvaluationMapper {

	public ProjectProposalAIEvaluationResponse toResponse(ProjectProposalAIEvaluationResult result) {
		AIEvaluation evaluation = result.getEvaluation();

		return new ProjectProposalAIEvaluationResponse(
				result.getId(),
				evaluation.getId(),
				result.getProposalId(),
				result.getDocumentVersionId(),
				evaluation.getStatus(),
				evaluation.getProvider(),
				evaluation.getModel(),
				result.getOverallScore(),
				result.getMaxScore(),
				result.getReadinessLevel(),
				result.getRecommendation(),
				result.getSummary(),
				copyList(result.getStrengths()),
				copyList(result.getWeaknesses()),
				copyList(result.getMissingSections()),
				copyList(result.getRiskNotes()),
				copyList(result.getSuggestedRevisions()),
				toCriteriaScoreResponses(result.getCriteriaScores()),
				result.getCreatedAt(),
				evaluation.getCompletedAt()
		);
	}

	private List<AICriteriaScoreResponse> toCriteriaScoreResponses(List<AICriteriaScore> scores) {
		if (scores == null) {
			return List.of();
		}
		return scores.stream()
				.map(score -> new AICriteriaScoreResponse(
						score.getCriterion(),
						score.getScore(),
						score.getMaxScore(),
						score.getRationale()))
				.toList();
	}

	private List<String> copyList(List<String> values) {
		if (values == null) {
			return List.of();
		}
		return List.copyOf(values);
	}
}
