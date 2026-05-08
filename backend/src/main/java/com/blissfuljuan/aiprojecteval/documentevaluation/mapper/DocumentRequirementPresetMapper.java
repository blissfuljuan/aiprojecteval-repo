package com.blissfuljuan.aiprojecteval.documentevaluation.mapper;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementPresetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.PresetDocumentRequirementResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementPreset;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentTemplate;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.EvaluationRubric;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.PresetDocumentRequirement;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import java.util.Comparator;
import java.util.List;

public final class DocumentRequirementPresetMapper {

	private DocumentRequirementPresetMapper() {
	}

	public static DocumentRequirementPresetResponse toResponse(DocumentRequirementPreset preset) {
		User createdBy = preset.getCreatedBy();

		return new DocumentRequirementPresetResponse(
				preset.getId(),
				preset.getName(),
				preset.getDescription(),
				preset.getCategory(),
				preset.getVisibility(),
				preset.getStatus(),
				createdBy == null ? null : createdBy.getId(),
				createdBy == null ? null : formatUserName(createdBy),
				createdBy == null ? null : createdBy.getEmail(),
				preset.getCreatedAt(),
				preset.getUpdatedAt(),
				preset.getDocumentRequirements()
						.stream()
						.sorted(Comparator.comparing(PresetDocumentRequirement::getSortOrder)
								.thenComparing(requirement -> requirement.getId() == null ? 0L : requirement.getId()))
						.map(DocumentRequirementPresetMapper::toRequirementResponse)
						.toList()
		);
	}

	public static PresetDocumentRequirementResponse toRequirementResponse(PresetDocumentRequirement requirement) {
		DocumentTemplate template = requirement.getTemplate();
		EvaluationRubric rubric = requirement.getRubric();

		return new PresetDocumentRequirementResponse(
				requirement.getId(),
				requirement.getPreset() == null ? null : requirement.getPreset().getId(),
				requirement.getName(),
				requirement.getDescription(),
				requirement.isRequired(),
				List.copyOf(requirement.getAllowedFileTypes()),
				requirement.getSortOrder(),
				template == null ? null : template.getId(),
				template == null ? null : template.getName(),
				rubric == null ? null : rubric.getId(),
				rubric == null ? null : rubric.getName(),
				requirement.getCreatedAt(),
				requirement.getUpdatedAt()
		);
	}

	private static String formatUserName(User user) {
		StringBuilder name = new StringBuilder(user.getFirstName());
		if (user.getMiddleName() != null && !user.getMiddleName().isBlank()) {
			name.append(' ').append(user.getMiddleName());
		}
		name.append(' ').append(user.getLastName());
		return name.toString();
	}
}
