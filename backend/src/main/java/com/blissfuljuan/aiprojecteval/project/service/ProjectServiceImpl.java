package com.blissfuljuan.aiprojecteval.project.service;

import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.service.AuthService;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectRequest;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import com.blissfuljuan.aiprojecteval.project.mapper.ProjectMapper;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final ProjectMapper projectMapper;
	private final AuthService authService;

	ProjectServiceImpl(
			ProjectRepository projectRepository,
			ProjectMapper projectMapper,
			AuthService authService) {
		this.projectRepository = projectRepository;
		this.projectMapper = projectMapper;
		this.authService = authService;
	}

	@Override
	@Transactional
	public ProjectResponse create(String currentUserEmail, ProjectRequest request) {
		UserResponse owner = authService.getCurrentUser(currentUserEmail);
		Project project = new Project(
				owner.id(),
				owner.email(),
				request.title(),
				request.description(),
				request.repositoryUrl()
		);

		return projectMapper.toResponse(projectRepository.save(project));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProjectResponse> findAll(String currentUserEmail) {
		UserResponse owner = authService.getCurrentUser(currentUserEmail);

		return projectRepository.findByOwnerUserIdOrderByCreatedAtDesc(owner.id())
				.stream()
				.map(projectMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProjectResponse findById(String currentUserEmail, Long id) {
		UserResponse owner = authService.getCurrentUser(currentUserEmail);

		return projectMapper.toResponse(findOwnedProject(id, owner.id()));
	}

	@Override
	@Transactional
	public ProjectResponse update(String currentUserEmail, Long id, ProjectRequest request) {
		UserResponse owner = authService.getCurrentUser(currentUserEmail);
		Project project = findOwnedProject(id, owner.id());
		project.setTitle(request.title());
		project.setDescription(request.description());
		project.setRepositoryUrl(request.repositoryUrl());

		return projectMapper.toResponse(projectRepository.save(project));
	}

	@Override
	@Transactional
	public void delete(String currentUserEmail, Long id) {
		UserResponse owner = authService.getCurrentUser(currentUserEmail);
		Project project = findOwnedProject(id, owner.id());
		projectRepository.delete(project);
	}

	private Project findOwnedProject(Long id, Long ownerUserId) {
		return projectRepository.findByIdAndOwnerUserId(id, ownerUserId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}
}
