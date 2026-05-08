package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRequirementSetRepository extends JpaRepository<DocumentRequirementSet, Long> {

	List<DocumentRequirementSet> findByStatus(ConfigurationStatus status);

	List<DocumentRequirementSet> findByOwnerInstructorId(Long ownerInstructorId);
}
