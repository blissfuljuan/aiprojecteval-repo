package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import org.springframework.core.io.Resource;

public record DocumentSubmissionFileResource(
		Resource resource,
		String originalFileName,
		String contentType
) {
}
