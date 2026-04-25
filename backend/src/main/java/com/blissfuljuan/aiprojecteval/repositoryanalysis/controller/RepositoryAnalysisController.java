package com.blissfuljuan.aiprojecteval.repositoryanalysis.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.repositoryanalysis.dto.RepositoryAnalysisResponse;
import com.blissfuljuan.aiprojecteval.repositoryanalysis.service.RepositoryAnalysisService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repository-analyses")
public class RepositoryAnalysisController {

	private final RepositoryAnalysisService repositoryAnalysisService;

	public RepositoryAnalysisController(RepositoryAnalysisService repositoryAnalysisService) {
		this.repositoryAnalysisService = repositoryAnalysisService;
	}

	@GetMapping
	public ApiResponse<List<RepositoryAnalysisResponse>> findAll() {
		return ApiResponse.ok(repositoryAnalysisService.findAll());
	}
}
