package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {

	List<DocumentTemplate> findByStatus(ConfigurationStatus status);

	List<DocumentTemplate> findByOwnerInstructorId(Long ownerInstructorId);
}
