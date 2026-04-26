package com.blissfuljuan.aiprojecteval.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.TestDataFactory;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.service.AuthService;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectRequest;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import com.blissfuljuan.aiprojecteval.project.mapper.ProjectMapper;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private AuthService authService;

	private ProjectServiceImpl projectService;

	@BeforeEach
	void setUp() {
		projectService = new ProjectServiceImpl(projectRepository, new ProjectMapper(), authService);
	}

	@Test
	void shouldCreateProjectForCurrentUser() {
		UserResponse owner = TestDataFactory.createUserResponse(Role.STUDENT);
		ProjectRequest request = TestDataFactory.createProjectRequest();
		Project savedProject = TestDataFactory.createProject(1L);
		when(authService.getCurrentUser("admin@example.com")).thenReturn(owner);
		when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

		ProjectResponse response = projectService.create("admin@example.com", request);

		assertThat(response.title()).isEqualTo("Capstone Portal");
		assertThat(response.ownerUserId()).isEqualTo(1L);
		ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
		verify(projectRepository).save(projectCaptor.capture());
		assertThat(projectCaptor.getValue().getOwnerEmail()).isEqualTo("admin@example.com");
	}

	@Test
	void shouldFindProjectsOwnedByCurrentUser() {
		UserResponse owner = TestDataFactory.createUserResponse(Role.STUDENT);
		Project project = TestDataFactory.createProject(1L);
		when(authService.getCurrentUser("admin@example.com")).thenReturn(owner);
		when(projectRepository.findByOwnerUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(project));

		List<ProjectResponse> response = projectService.findAll("admin@example.com");

		assertThat(response).hasSize(1);
		assertThat(response.get(0).id()).isEqualTo(1L);
	}

	@Test
	void shouldFindProjectsByOwnerId() {
		Project project = TestDataFactory.createProject(1L);
		when(projectRepository.findByOwnerUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(project));

		List<ProjectResponse> response = projectService.findByOwner(1L);

		assertThat(response).hasSize(1);
		assertThat(response.get(0).ownerUserId()).isEqualTo(1L);
	}

	@Test
	void shouldUpdateOwnedProject() {
		UserResponse owner = TestDataFactory.createUserResponse(Role.STUDENT);
		Project project = TestDataFactory.createProject(1L);
		ProjectRequest request = new ProjectRequest("Updated", "Updated description", "https://github.com/example/updated");
		when(authService.getCurrentUser("admin@example.com")).thenReturn(owner);
		when(projectRepository.findByIdAndOwnerUserId(1L, 1L)).thenReturn(Optional.of(project));
		when(projectRepository.save(project)).thenReturn(project);

		ProjectResponse response = projectService.update("admin@example.com", 1L, request);

		assertThat(response.title()).isEqualTo("Updated");
		assertThat(project.getRepositoryUrl()).isEqualTo("https://github.com/example/updated");
	}

	@Test
	void shouldRejectProjectFromAnotherOwner() {
		UserResponse owner = TestDataFactory.createUserResponse(Role.STUDENT);
		when(authService.getCurrentUser("admin@example.com")).thenReturn(owner);
		when(projectRepository.findByIdAndOwnerUserId(99L, 1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> projectService.findById("admin@example.com", 99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Project not found");
	}
}
