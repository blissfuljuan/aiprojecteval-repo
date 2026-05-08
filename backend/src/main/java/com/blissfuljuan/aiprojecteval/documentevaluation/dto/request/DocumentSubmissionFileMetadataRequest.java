package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentSubmissionFileMetadataRequest(
		@NotBlank(message = "Original file name is required")
		@Size(max = 255, message = "Original file name must not exceed 255 characters")
		String originalFileName,

		@Size(max = 150, message = "Content type must not exceed 150 characters")
		String contentType,

		@Min(value = 0, message = "File size must be greater than or equal to 0")
		Long fileSize,

		@Size(max = 20, message = "File extension must not exceed 20 characters")
		String fileExtension,

		@Size(max = 1000, message = "File URL must not exceed 1000 characters")
		String fileUrl,

		@Size(max = 1000, message = "Storage path must not exceed 1000 characters")
		String storagePath,

		@Size(max = 255, message = "Checksum must not exceed 255 characters")
		String checksum
) {
}
