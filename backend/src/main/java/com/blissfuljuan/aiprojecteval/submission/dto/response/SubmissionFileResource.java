package com.blissfuljuan.aiprojecteval.submission.dto.response;

import org.springframework.core.io.Resource;

public record SubmissionFileResource(
		Resource resource,
		String originalFileName,
		String contentType
) {
}
