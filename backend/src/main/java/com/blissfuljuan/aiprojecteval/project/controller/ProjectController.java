package com.blissfuljuan.aiprojecteval.project.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectRequest;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import com.blissfuljuan.aiprojecteval.project.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@PostMapping
	public ApiResponse<ProjectResponse> create(
			Authentication authentication,
			@Valid @RequestBody ProjectRequest request) {
		return ApiResponse.ok("Project created", projectService.create(authentication.getName(), request));
	}

	@GetMapping
	public ApiResponse<List<ProjectResponse>> findAll(Authentication authentication) {
		return ApiResponse.ok(projectService.findAll(authentication.getName()));
	}

	@GetMapping("/{id}")
	public ApiResponse<ProjectResponse> findById(Authentication authentication, @PathVariable Long id) {
		return ApiResponse.ok(projectService.findById(authentication.getName(), id));
	}

	@PutMapping("/{id}")
	public ApiResponse<ProjectResponse> update(
			Authentication authentication,
			@PathVariable Long id,
			@Valid @RequestBody ProjectRequest request) {
		return ApiResponse.ok("Project updated", projectService.update(authentication.getName(), id, request));
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> delete(Authentication authentication, @PathVariable Long id) {
		projectService.delete(authentication.getName(), id);

		return ApiResponse.ok("Project deleted", null);
	}
}
