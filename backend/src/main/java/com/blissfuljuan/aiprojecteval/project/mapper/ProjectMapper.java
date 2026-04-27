package com.blissfuljuan.aiprojecteval.project.mapper;

import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

	public ProjectResponse toResponse(Project project) {
		return new ProjectResponse(
				project.getId(),
				project.getOwnerUserId(),
				project.getOwnerEmail(),
				project.getTitle(),
				project.getDescription(),
				project.getRepositoryUrl(),
				project.getProjectProposalId(),
				project.getCreatedAt(),
				project.getUpdatedAt()
		);
	}
}
