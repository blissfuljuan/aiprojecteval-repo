package com.blissfuljuan.aiprojecteval.projectproposal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectProposalUpdateRequest(
		@NotBlank @Size(max = 150) String title,
		@NotBlank String problemStatement,
		@NotBlank String objectives,
		String targetUsers,
		@NotBlank String proposedFeatures,
		String technologyStack,
		String expectedOutput
) {
}
