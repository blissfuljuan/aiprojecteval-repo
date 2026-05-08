package com.blissfuljuan.aiprojecteval.submission.storage;

public record StoredFile(
		String originalFileName,
		String storedFileName,
		String storagePath,
		String contentType,
		Long fileSize,
		String fileExtension,
		String checksum
) {
}
