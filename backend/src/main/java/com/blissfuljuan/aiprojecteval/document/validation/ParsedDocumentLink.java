package com.blissfuljuan.aiprojecteval.document.validation;

import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;

public record ParsedDocumentLink(
		DocumentValidationStatus validationStatus,
		DocumentProvider provider,
		String fileId,
		String message
) {

	public static ParsedDocumentLink valid(DocumentProvider provider, String fileId) {
		return new ParsedDocumentLink(DocumentValidationStatus.VALID, provider, fileId, "Google document link parsed");
	}

	public static ParsedDocumentLink invalid(String message) {
		return new ParsedDocumentLink(DocumentValidationStatus.INVALID_URL, DocumentProvider.UNKNOWN, null, message);
	}

	public boolean isValid() {
		return validationStatus == DocumentValidationStatus.VALID;
	}
}
