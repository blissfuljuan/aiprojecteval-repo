package com.blissfuljuan.aiprojecteval.document.service;

import com.blissfuljuan.aiprojecteval.document.dto.DocumentLinkSubmitRequest;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentSummaryResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentVersionResponse;
import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import java.util.List;

public interface DocumentService {

	DocumentResponse submitExternalLink(DocumentLinkSubmitRequest request);

	DocumentResponse findById(Long documentId);

	List<DocumentSummaryResponse> findByContext(DocumentContextType contextType, Long contextId);

	List<DocumentResponse> findByContextAndDocumentType(
			DocumentContextType contextType,
			Long contextId,
			DocumentType documentType);

	List<DocumentVersionResponse> findVersions(Long documentId);

	DocumentVersion findVersionById(Long versionId);

	DocumentVersionResponse validateVersion(Long documentId, Long versionId);

	DocumentVersionResponse extractVersionText(Long documentId, Long versionId);

	DocumentResponse validateCurrentVersion(Long documentId);

	void delete(Long documentId);
}
