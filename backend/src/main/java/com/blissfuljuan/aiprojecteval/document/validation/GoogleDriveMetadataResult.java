package com.blissfuljuan.aiprojecteval.document.validation;

import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;

public record GoogleDriveMetadataResult(
		DocumentValidationStatus status,
		GoogleDriveFileMetadata metadata,
		String message
) {

	public static GoogleDriveMetadataResult success(GoogleDriveFileMetadata metadata) {
		return new GoogleDriveMetadataResult(DocumentValidationStatus.VALID, metadata, "Google Drive metadata fetched");
	}

	public static GoogleDriveMetadataResult failure(DocumentValidationStatus status, String message) {
		return new GoogleDriveMetadataResult(status, null, message);
	}

	public boolean isSuccess() {
		return status == DocumentValidationStatus.VALID;
	}
}
