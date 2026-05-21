package com.blissfuljuan.aiprojecteval.projectproposal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectProposalDocumentLinkRequest(
		@Size(max = 150) String title,
		String description,
		@NotBlank String documentUrl
) {
}
