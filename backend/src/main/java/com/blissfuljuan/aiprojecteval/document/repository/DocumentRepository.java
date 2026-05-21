package com.blissfuljuan.aiprojecteval.document.repository;

import com.blissfuljuan.aiprojecteval.document.model.Document;
import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	Optional<Document> findByContextTypeAndContextIdAndDocumentType(
			DocumentContextType contextType,
			Long contextId,
			DocumentType documentType);

	List<Document> findByContextTypeAndContextId(DocumentContextType contextType, Long contextId);

	List<Document> findByContextTypeAndContextIdAndDocumentTypeOrderByUpdatedAtDesc(
			DocumentContextType contextType,
			Long contextId,
			DocumentType documentType);
}
