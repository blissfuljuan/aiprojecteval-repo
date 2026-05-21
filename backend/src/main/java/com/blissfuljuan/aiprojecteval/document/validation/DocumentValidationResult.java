package com.blissfuljuan.aiprojecteval.document.validation;

import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;

public record DocumentValidationResult(
		DocumentValidationStatus status,
		String message,
		DocumentProvider provider,
		String externalFileId,
		String fileName,
		String mimeType,
		Long fileSizeBytes
) {

	public static DocumentValidationResult failure(
			DocumentValidationStatus status,
			String message,
			DocumentProvider provider,
			String externalFileId) {
		return new DocumentValidationResult(status, message, provider, externalFileId, null, null, null);
	}

	public static DocumentValidationResult valid(
			String message,
			DocumentProvider provider,
			String externalFileId,
			GoogleDriveFileMetadata metadata) {
		return new DocumentValidationResult(
				DocumentValidationStatus.VALID,
				message,
				provider,
				externalFileId,
				metadata.name(),
				metadata.mimeType(),
				metadata.size());
	}
}
