package com.blissfuljuan.aiprojecteval.evaluation.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.evaluation.dto.EvaluationResponse;
import com.blissfuljuan.aiprojecteval.evaluation.service.EvaluationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

	private final EvaluationService evaluationService;

	public EvaluationController(EvaluationService evaluationService) {
		this.evaluationService = evaluationService;
	}

	@GetMapping
	public ApiResponse<List<EvaluationResponse>> findAll() {
		return ApiResponse.ok(evaluationService.findAll());
	}
}
