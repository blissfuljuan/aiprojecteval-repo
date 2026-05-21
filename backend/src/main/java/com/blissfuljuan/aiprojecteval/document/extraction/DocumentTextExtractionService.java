package com.blissfuljuan.aiprojecteval.document.extraction;

import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;

public interface DocumentTextExtractionService {

	DocumentTextExtractionResult extract(DocumentVersion version);
}
