package com.blissfuljuan.aiprojecteval.document.validation;

import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;

public interface DocumentLinkValidationService {

	DocumentValidationResult validate(DocumentVersion version);
}
