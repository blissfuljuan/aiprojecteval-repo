package com.blissfuljuan.aiprojecteval.document.dto;

import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DocumentLinkSubmitRequest(
		@NotNull DocumentContextType contextType,
		@NotNull Long contextId,
		@NotNull DocumentType documentType,
		@NotBlank @Size(max = 150) String title,
		String description,
		@NotBlank String documentUrl
) {
}
