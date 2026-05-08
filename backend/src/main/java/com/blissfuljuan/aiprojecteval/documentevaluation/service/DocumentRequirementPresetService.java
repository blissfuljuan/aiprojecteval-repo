package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentRequirementPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.PresetDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentRequirementPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdatePresetDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementPresetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
import java.util.List;

public interface DocumentRequirementPresetService {

	DocumentRequirementPresetResponse createPreset(String currentUserEmail, CreateDocumentRequirementPresetRequest request);

	DocumentRequirementPresetResponse getPresetById(String currentUserEmail, Long id);

	List<DocumentRequirementPresetResponse> listPresets(
			String currentUserEmail,
			ConfigurationStatus status,
			PresetVisibility visibility,
			String category,
			String search);

	DocumentRequirementPresetResponse updatePreset(Long id, UpdateDocumentRequirementPresetRequest request);

	DocumentRequirementPresetResponse archivePreset(Long id);

	DocumentRequirementPresetResponse activatePreset(Long id);

	DocumentRequirementPresetResponse addDocumentRequirement(
			Long presetId,
			PresetDocumentRequirementRequest request);

	DocumentRequirementPresetResponse updateDocumentRequirement(
			Long presetId,
			Long requirementId,
			UpdatePresetDocumentRequirementRequest request);

	void removeDocumentRequirement(Long presetId, Long requirementId);
}
