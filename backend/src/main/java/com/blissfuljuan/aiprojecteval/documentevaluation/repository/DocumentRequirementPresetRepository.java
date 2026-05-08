package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementPreset;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRequirementPresetRepository extends JpaRepository<DocumentRequirementPreset, Long> {

	List<DocumentRequirementPreset> findByStatus(ConfigurationStatus status);

	List<DocumentRequirementPreset> findByVisibility(PresetVisibility visibility);

	List<DocumentRequirementPreset> findByCreatedById(Long createdById);

	List<DocumentRequirementPreset> findByStatusAndVisibility(
			ConfigurationStatus status,
			PresetVisibility visibility);

	List<DocumentRequirementPreset> findByCategoryIgnoreCase(String category);

	List<DocumentRequirementPreset> findByNameContainingIgnoreCase(String name);
}
