package com.blissfuljuan.aiprojecteval.document.repository;

import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {

	List<DocumentVersion> findByDocumentIdOrderByVersionNumberDesc(Long documentId);

	Optional<DocumentVersion> findTopByDocumentIdOrderByVersionNumberDesc(Long documentId);

	void deleteByDocumentId(Long documentId);
}
