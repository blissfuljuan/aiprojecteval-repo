package com.blissfuljuan.aiprojecteval.projectproposal.dto;

import com.blissfuljuan.aiprojecteval.projectproposal.model.ProposalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProposalDecisionRequest(
		@NotNull ProposalStatus decision,
		@NotBlank String remarks
) {
}
