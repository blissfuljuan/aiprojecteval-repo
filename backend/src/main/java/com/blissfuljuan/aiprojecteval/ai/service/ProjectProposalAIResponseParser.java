package com.blissfuljuan.aiprojecteval.ai.service;

import com.blissfuljuan.aiprojecteval.ai.model.AICriteriaScore;
import com.blissfuljuan.aiprojecteval.ai.model.ProposalAIRecommendation;
import com.blissfuljuan.aiprojecteval.ai.model.ProposalReadinessLevel;
import com.blissfuljuan.aiprojecteval.ai.model.ProjectProposalAIEvaluationResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjectProposalAIResponseParser {

	private final ObjectMapper objectMapper = new ObjectMapper();

	public ProjectProposalAIEvaluationResult parse(String rawResponse) {
		try {
			ParsedResponse parsed = objectMapper.readValue(rawResponse, ParsedResponse.class);
			return toResult(parsed);
		} catch (Exception exception) {
			return fallbackResult();
		}
	}

	private ProjectProposalAIEvaluationResult toResult(ParsedResponse parsed) {
		ProjectProposalAIEvaluationResult result = new ProjectProposalAIEvaluationResult();
		result.setOverallScore(parsed.overallScore());
		result.setMaxScore(defaultNumber(parsed.maxScore(), 100));
		result.setReadinessLevel(parseEnum(parsed.readinessLevel(), ProposalReadinessLevel.NOT_EVALUABLE));
		result.setRecommendation(parseEnum(parsed.recommendation(), ProposalAIRecommendation.MANUAL_REVIEW_REQUIRED));
		result.setSummary(parsed.summary());
		result.setStrengths(copyList(parsed.strengths()));
		result.setWeaknesses(copyList(parsed.weaknesses()));
		result.setMissingSections(copyList(parsed.missingSections()));
		result.setRiskNotes(copyList(parsed.riskNotes()));
		result.setSuggestedRevisions(copyList(parsed.suggestedRevisions()));
		result.setCriteriaScores(toCriteriaScores(parsed.criteriaScores()));

		return result;
	}

	private ProjectProposalAIEvaluationResult fallbackResult() {
		ProjectProposalAIEvaluationResult result = new ProjectProposalAIEvaluationResult();
		result.setOverallScore(null);
		result.setMaxScore(100);
		result.setReadinessLevel(ProposalReadinessLevel.NOT_EVALUABLE);
		result.setRecommendation(ProposalAIRecommendation.MANUAL_REVIEW_REQUIRED);
		result.setSummary("The AI response could not be parsed. Manual review is required.");
		result.setStrengths(List.of());
		result.setWeaknesses(List.of("AI response parsing failed."));
		result.setMissingSections(List.of());
		result.setRiskNotes(List.of("The stored raw AI response should be inspected by an administrator."));
		result.setSuggestedRevisions(List.of("Perform a manual proposal review."));
		result.setCriteriaScores(List.of());

		return result;
	}

	private List<AICriteriaScore> toCriteriaScores(List<ParsedCriteriaScore> parsedScores) {
		if (parsedScores == null) {
			return List.of();
		}

		List<AICriteriaScore> scores = new ArrayList<>();
		for (ParsedCriteriaScore parsedScore : parsedScores) {
			AICriteriaScore score = new AICriteriaScore();
			score.setCriterion(parsedScore.criterion());
			score.setScore(parsedScore.score());
			score.setMaxScore(defaultNumber(parsedScore.maxScore(), 10));
			score.setRationale(parsedScore.rationale());
			scores.add(score);
		}
		return scores;
	}

	private <T extends Enum<T>> T parseEnum(String value, T fallback) {
		if (value == null || value.isBlank()) {
			return fallback;
		}
		try {
			return Enum.valueOf(fallback.getDeclaringClass(), value);
		} catch (IllegalArgumentException exception) {
			return fallback;
		}
	}

	private Integer defaultNumber(Integer value, Integer fallback) {
		return value == null ? fallback : value;
	}

	private List<String> copyList(List<String> values) {
		if (values == null) {
			return List.of();
		}
		return List.copyOf(values);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	private record ParsedResponse(
			Integer overallScore,
			Integer maxScore,
			String readinessLevel,
			String recommendation,
			String summary,
			List<String> strengths,
			List<String> weaknesses,
			List<String> missingSections,
			List<String> riskNotes,
			List<String> suggestedRevisions,
			List<ParsedCriteriaScore> criteriaScores
	) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	private record ParsedCriteriaScore(
			String criterion,
			Integer score,
			Integer maxScore,
			String rationale
	) {
	}
}
