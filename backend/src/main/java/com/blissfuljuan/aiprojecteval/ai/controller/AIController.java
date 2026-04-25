package com.blissfuljuan.aiprojecteval.ai.controller;

import com.blissfuljuan.aiprojecteval.ai.dto.AIResponse;
import com.blissfuljuan.aiprojecteval.ai.service.AIService;
import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {

	private final AIService aiService;

	public AIController(AIService aiService) {
		this.aiService = aiService;
	}

	@GetMapping("/providers")
	public ApiResponse<List<AIResponse>> providers() {
		return ApiResponse.ok(aiService.availableProviders());
	}
}
