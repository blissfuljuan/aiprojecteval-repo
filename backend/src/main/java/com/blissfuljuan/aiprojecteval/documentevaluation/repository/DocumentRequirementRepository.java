package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRequirementRepository extends JpaRepository<DocumentRequirement, Long> {

	List<DocumentRequirement> findByRequirementSetIdOrderBySortOrderAsc(Long requirementSetId);
}
