package com.blissfuljuan.aiprojecteval.project.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.blissfuljuan.aiprojecteval.project.model.Project;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ProjectRepositoryTest {

	@Autowired
	private ProjectRepository projectRepository;

	@Test
	void shouldFindProjectsByOwnerOnly() {
		projectRepository.save(new Project(1L, "owner@example.com", "Owned", null, null));
		projectRepository.save(new Project(2L, "other@example.com", "Other", null, null));

		List<Project> projects = projectRepository.findByOwnerUserIdOrderByCreatedAtDesc(1L);

		assertThat(projects).hasSize(1);
		assertThat(projects.get(0).getOwnerEmail()).isEqualTo("owner@example.com");
	}

	@Test
	void shouldFindProjectByIdAndOwner() {
		Project savedProject = projectRepository.save(
				new Project(1L, "owner@example.com", "Owned", null, null));

		Optional<Project> foundProject = projectRepository.findByIdAndOwnerUserId(savedProject.getId(), 1L);
		Optional<Project> missingProject = projectRepository.findByIdAndOwnerUserId(savedProject.getId(), 2L);

		assertThat(foundProject).isPresent();
		assertThat(missingProject).isEmpty();
	}
}
