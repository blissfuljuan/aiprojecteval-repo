package com.blissfuljuan.aiprojecteval.deploymentvalidation.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.deploymentvalidation.dto.DeploymentValidationResponse;
import com.blissfuljuan.aiprojecteval.deploymentvalidation.service.DeploymentValidationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deployment-validations")
public class DeploymentValidationController {

	private final DeploymentValidationService deploymentValidationService;

	public DeploymentValidationController(DeploymentValidationService deploymentValidationService) {
		this.deploymentValidationService = deploymentValidationService;
	}

	@GetMapping
	public ApiResponse<List<DeploymentValidationResponse>> findAll() {
		return ApiResponse.ok(deploymentValidationService.findAll());
	}
}
