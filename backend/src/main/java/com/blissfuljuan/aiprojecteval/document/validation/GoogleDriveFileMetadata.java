package com.blissfuljuan.aiprojecteval.document.validation;

public record GoogleDriveFileMetadata(
		String id,
		String name,
		String mimeType,
		Long size,
		String webViewLink,
		Boolean canDownload
) {
}
