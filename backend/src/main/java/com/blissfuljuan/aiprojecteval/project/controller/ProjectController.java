package com.blissfuljuan.aiprojecteval.project.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import com.blissfuljuan.aiprojecteval.project.service.ProjectService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@GetMapping
	public ApiResponse<List<ProjectResponse>> findAll() {
		return ApiResponse.ok(projectService.findAll());
	}
}
