package com.blissfuljuan.aiprojecteval.evaluation.service;

import com.blissfuljuan.aiprojecteval.evaluation.dto.EvaluationResponse;
import java.util.List;

public interface EvaluationService {

	List<EvaluationResponse> findAll();
}
