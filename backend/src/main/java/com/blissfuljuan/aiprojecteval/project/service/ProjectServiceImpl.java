package com.blissfuljuan.aiprojecteval.project.service;

import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
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

		List<Project> projects = owner.role() == Role.STUDENT
				? projectRepository.findByOwnerUserIdOrderByCreatedAtDesc(owner.id())
				: projectRepository.findAllByOrderByCreatedAtDesc();

		return projects
				.stream()
				.map(projectMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProjectResponse> findByOwner(Long ownerUserId) {
		return projectRepository.findByOwnerUserIdOrderByCreatedAtDesc(ownerUserId)
				.stream()
				.map(projectMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProjectResponse findById(String currentUserEmail, Long id) {
		UserResponse owner = authService.getCurrentUser(currentUserEmail);

		return projectMapper.toResponse(findReadableProject(id, owner));
	}

	@Override
	@Transactional
	public ProjectResponse update(String currentUserEmail, Long id, ProjectRequest request) {
		UserResponse owner = authService.getCurrentUser(currentUserEmail);
		Project project = findWritableProject(id, owner);
		project.setTitle(request.title());
		project.setDescription(request.description());
		project.setRepositoryUrl(request.repositoryUrl());

		return projectMapper.toResponse(projectRepository.save(project));
	}

	@Override
	@Transactional
	public void delete(String currentUserEmail, Long id) {
		UserResponse owner = authService.getCurrentUser(currentUserEmail);
		Project project = findWritableProject(id, owner);
		projectRepository.delete(project);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByProjectProposalId(Long projectProposalId) {
		return projectRepository.existsByProjectProposalId(projectProposalId);
	}

	@Override
	@Transactional
	public ProjectResponse createFromApprovedProposal(
			Long ownerUserId,
			String ownerEmail,
			String title,
			String description,
			Long projectProposalId) {
		Project project = new Project(ownerUserId, ownerEmail, title, description, null);
		project.setProjectProposalId(projectProposalId);

		return projectMapper.toResponse(projectRepository.save(project));
	}

	private Project findReadableProject(Long id, UserResponse currentUser) {
		if (currentUser.role() == Role.STUDENT) {
			return findOwnedProject(id, currentUser.id());
		}

		return projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	private Project findWritableProject(Long id, UserResponse currentUser) {
		if (currentUser.role() == Role.STUDENT) {
			return findOwnedProject(id, currentUser.id());
		}
		if (currentUser.role() == Role.ADMIN || currentUser.role() == Role.INSTRUCTOR) {
			return projectRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
		}

		throw new BadRequestException("User is not allowed to modify this project");
	}

	private Project findOwnedProject(Long id, Long ownerUserId) {
		return projectRepository.findByIdAndOwnerUserId(id, ownerUserId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}
}
