package com.blissfuljuan.aiprojecteval.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
		@NotBlank @Size(max = 255) String title,
		@Size(max = 2000) String description,
		@Size(max = 1000) String repositoryUrl
) {
}
