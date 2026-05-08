package com.blissfuljuan.aiprojecteval.documentevaluation.storage;

public record StoredDocumentFile(
		String originalFileName,
		String storedFileName,
		String storagePath,
		String contentType,
		Long fileSize,
		String fileExtension,
		String checksum
) {
}
