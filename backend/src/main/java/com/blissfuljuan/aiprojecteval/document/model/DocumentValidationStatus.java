package com.blissfuljuan.aiprojecteval.document.model;

public enum DocumentValidationStatus {
	PENDING,
	VALID,
	INVALID_URL,
	INACCESSIBLE,
	UNSUPPORTED_FILE_TYPE,
	TOO_LARGE,
	NOT_A_DOCUMENT,
	FAILED
}
