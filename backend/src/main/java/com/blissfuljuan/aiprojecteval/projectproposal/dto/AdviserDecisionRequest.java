package com.blissfuljuan.aiprojecteval.projectproposal.dto;

import com.blissfuljuan.aiprojecteval.projectproposal.model.ProposalStatus;
import jakarta.validation.constraints.NotNull;

public record AdviserDecisionRequest(
		@NotNull ProposalStatus decision,
		String remarks
) {
}
