package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.PresetDocumentRequirement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresetDocumentRequirementRepository extends JpaRepository<PresetDocumentRequirement, Long> {

	List<PresetDocumentRequirement> findByPresetIdOrderBySortOrderAsc(Long presetId);
}
