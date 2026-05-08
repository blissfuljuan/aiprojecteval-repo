package com.blissfuljuan.aiprojecteval.documentevaluation.mapper;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentTemplateResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.EvaluationRubricResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.RubricCriterionResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.RubricLevelResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.TemplateSectionResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentTemplate;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.EvaluationRubric;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricCriterion;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricLevel;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.TemplateSection;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import java.util.Comparator;
import java.util.List;

public final class DocumentRequirementSetMapper {

	private DocumentRequirementSetMapper() {
	}

	public static DocumentRequirementSetResponse toResponse(DocumentRequirementSet requirementSet) {
		User ownerInstructor = requirementSet.getOwnerInstructor();

		return new DocumentRequirementSetResponse(
				requirementSet.getId(),
				requirementSet.getName(),
				requirementSet.getDescription(),
				requirementSet.getSourcePresetId(),
				ownerInstructor == null ? null : ownerInstructor.getId(),
				ownerInstructor == null ? null : formatUserName(ownerInstructor),
				ownerInstructor == null ? null : ownerInstructor.getEmail(),
				requirementSet.getStatus(),
				requirementSet.getCreatedAt(),
				requirementSet.getUpdatedAt(),
				requirementSet.getDocumentRequirements()
						.stream()
						.sorted(Comparator.comparing(DocumentRequirement::getSortOrder)
								.thenComparing(requirement -> requirement.getId() == null ? 0L : requirement.getId()))
						.map(DocumentRequirementSetMapper::toRequirementResponse)
						.toList()
		);
	}

	public static DocumentRequirementSetSummaryResponse toSummaryResponse(DocumentRequirementSet requirementSet) {
		User ownerInstructor = requirementSet.getOwnerInstructor();

		return new DocumentRequirementSetSummaryResponse(
				requirementSet.getId(),
				requirementSet.getName(),
				requirementSet.getDescription(),
				requirementSet.getSourcePresetId(),
				ownerInstructor == null ? null : ownerInstructor.getId(),
				ownerInstructor == null ? null : formatUserName(ownerInstructor),
				ownerInstructor == null ? null : ownerInstructor.getEmail(),
				requirementSet.getStatus(),
				requirementSet.getCreatedAt(),
				requirementSet.getUpdatedAt(),
				requirementSet.getDocumentRequirements().size()
		);
	}

	public static DocumentRequirementResponse toRequirementResponse(DocumentRequirement requirement) {
		return new DocumentRequirementResponse(
				requirement.getId(),
				requirement.getRequirementSet() == null ? null : requirement.getRequirementSet().getId(),
				requirement.getName(),
				requirement.getDescription(),
				requirement.isRequired(),
				List.copyOf(requirement.getAllowedFileTypes()),
				requirement.getSortOrder(),
				toTemplateResponse(requirement.getTemplate()),
				toRubricResponse(requirement.getRubric()),
				requirement.getCreatedAt(),
				requirement.getUpdatedAt()
		);
	}

	private static DocumentTemplateResponse toTemplateResponse(DocumentTemplate template) {
		if (template == null) {
			return null;
		}
		User ownerInstructor = template.getOwnerInstructor();

		return new DocumentTemplateResponse(
				template.getId(),
				template.getName(),
				template.getDescription(),
				template.getSourcePresetTemplateId(),
				ownerInstructor == null ? null : ownerInstructor.getId(),
				ownerInstructor == null ? null : formatUserName(ownerInstructor),
				ownerInstructor == null ? null : ownerInstructor.getEmail(),
				template.getStatus(),
				template.getCreatedAt(),
				template.getUpdatedAt(),
				template.getSections()
						.stream()
						.sorted(Comparator.comparing(TemplateSection::getSortOrder)
								.thenComparing(section -> section.getId() == null ? 0L : section.getId()))
						.map(DocumentRequirementSetMapper::toSectionResponse)
						.toList()
		);
	}

	private static TemplateSectionResponse toSectionResponse(TemplateSection section) {
		return new TemplateSectionResponse(
				section.getId(),
				section.getTemplate() == null ? null : section.getTemplate().getId(),
				section.getTitle(),
				section.getDescription(),
				section.isRequired(),
				section.getSortOrder(),
				section.getMinimumWordCount(),
				section.getCreatedAt(),
				section.getUpdatedAt()
		);
	}

	private static EvaluationRubricResponse toRubricResponse(EvaluationRubric rubric) {
		if (rubric == null) {
			return null;
		}
		User ownerInstructor = rubric.getOwnerInstructor();

		return new EvaluationRubricResponse(
				rubric.getId(),
				rubric.getName(),
				rubric.getDescription(),
				rubric.getSourcePresetRubricId(),
				ownerInstructor == null ? null : ownerInstructor.getId(),
				ownerInstructor == null ? null : formatUserName(ownerInstructor),
				ownerInstructor == null ? null : ownerInstructor.getEmail(),
				rubric.getTotalPoints(),
				rubric.getScoringType(),
				rubric.getStatus(),
				rubric.getCreatedAt(),
				rubric.getUpdatedAt(),
				rubric.getCriteria()
						.stream()
						.sorted(Comparator.comparing(RubricCriterion::getSortOrder)
								.thenComparing(criterion -> criterion.getId() == null ? 0L : criterion.getId()))
						.map(DocumentRequirementSetMapper::toCriterionResponse)
						.toList()
		);
	}

	private static RubricCriterionResponse toCriterionResponse(RubricCriterion criterion) {
		return new RubricCriterionResponse(
				criterion.getId(),
				criterion.getRubric() == null ? null : criterion.getRubric().getId(),
				criterion.getName(),
				criterion.getDescription(),
				criterion.getMaxPoints(),
				criterion.getWeight(),
				criterion.getSortOrder(),
				criterion.getCreatedAt(),
				criterion.getUpdatedAt(),
				criterion.getLevels()
						.stream()
						.sorted(Comparator.comparing(RubricLevel::getSortOrder)
								.thenComparing(level -> level.getId() == null ? 0L : level.getId()))
						.map(DocumentRequirementSetMapper::toLevelResponse)
						.toList()
		);
	}

	private static RubricLevelResponse toLevelResponse(RubricLevel level) {
		return new RubricLevelResponse(
				level.getId(),
				level.getCriterion() == null ? null : level.getCriterion().getId(),
				level.getLevelName(),
				level.getDescription(),
				level.getPoints(),
				level.getSortOrder(),
				level.getCreatedAt(),
				level.getUpdatedAt()
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
