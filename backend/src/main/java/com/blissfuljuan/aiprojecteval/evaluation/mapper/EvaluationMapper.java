package com.blissfuljuan.aiprojecteval.evaluation.mapper;

import com.blissfuljuan.aiprojecteval.evaluation.dto.EvaluationResponse;
import com.blissfuljuan.aiprojecteval.evaluation.model.Evaluation;
import org.springframework.stereotype.Component;

@Component
public class EvaluationMapper {

	public EvaluationResponse toResponse(Evaluation evaluation) {
		return new EvaluationResponse(evaluation.getId(), null);
	}
}
