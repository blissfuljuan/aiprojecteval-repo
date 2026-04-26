package com.blissfuljuan.aiprojecteval.project.dto;

import java.time.LocalDateTime;

public record ProjectResponse(
		Long id,
		Long ownerUserId,
		String ownerEmail,
		String title,
		String description,
		String repositoryUrl,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
