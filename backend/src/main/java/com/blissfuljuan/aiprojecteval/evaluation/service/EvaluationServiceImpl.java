package com.blissfuljuan.aiprojecteval.evaluation.service;

import com.blissfuljuan.aiprojecteval.evaluation.dto.EvaluationResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class EvaluationServiceImpl implements EvaluationService {

	@Override
	public List<EvaluationResponse> findAll() {
		return List.of();
	}
}
