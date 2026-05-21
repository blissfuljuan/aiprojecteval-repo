package com.blissfuljuan.aiprojecteval.ai.dto;

import com.blissfuljuan.aiprojecteval.ai.model.AIProviderType;
import jakarta.validation.constraints.NotNull;

public record ProjectProposalAIEvaluationRequest(
		@NotNull
		Long documentVersionId,
		AIProviderType provider,
		Boolean forceReevaluate
) {
}
