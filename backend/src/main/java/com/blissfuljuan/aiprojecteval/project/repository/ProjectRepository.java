package com.blissfuljuan.aiprojecteval.project.repository;

import com.blissfuljuan.aiprojecteval.project.model.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	List<Project> findByOwnerUserIdOrderByCreatedAtDesc(Long ownerUserId);

	Optional<Project> findByIdAndOwnerUserId(Long id, Long ownerUserId);
}
