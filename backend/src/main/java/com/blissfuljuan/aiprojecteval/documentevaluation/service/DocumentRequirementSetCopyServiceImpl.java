package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CopyPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
import com.blissfuljuan.aiprojecteval.documentevaluation.mapper.DocumentRequirementSetMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementPreset;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentTemplate;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.EvaluationRubric;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.PresetDocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricCriterion;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricLevel;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.TemplateSection;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementPresetRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentTemplateRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.EvaluationRubricRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import java.util.LinkedHashSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentRequirementSetCopyServiceImpl implements DocumentRequirementSetCopyService {

	private final DocumentRequirementPresetRepository presetRepository;
	private final DocumentRequirementSetRepository requirementSetRepository;
	private final DocumentTemplateRepository templateRepository;
	private final EvaluationRubricRepository rubricRepository;
	private final UserRepository userRepository;

	DocumentRequirementSetCopyServiceImpl(
			DocumentRequirementPresetRepository presetRepository,
			DocumentRequirementSetRepository requirementSetRepository,
			DocumentTemplateRepository templateRepository,
			EvaluationRubricRepository rubricRepository,
			UserRepository userRepository) {
		this.presetRepository = presetRepository;
		this.requirementSetRepository = requirementSetRepository;
		this.templateRepository = templateRepository;
		this.rubricRepository = rubricRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public DocumentRequirementSetResponse copyPresetToRequirementSet(
			String currentUserEmail,
			Long presetId,
			CopyPresetRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementPreset preset = findPreset(presetId);
		if (!canViewPreset(currentUser, preset)) {
			throw new ResourceNotFoundException("Document requirement preset not found");
		}
		if (preset.getStatus() == ConfigurationStatus.ARCHIVED) {
			throw new BadRequestException("Archived presets cannot be copied");
		}

		DocumentRequirementSet requirementSet = new DocumentRequirementSet(request.name(), ConfigurationStatus.DRAFT);
		requirementSet.setDescription(request.description());
		requirementSet.setSourcePresetId(preset.getId());
		requirementSet.setOwnerInstructor(currentUser);

		for (PresetDocumentRequirement presetRequirement : preset.getDocumentRequirements()) {
			DocumentRequirement copiedRequirement = copyDocumentRequirement(presetRequirement, currentUser);
			requirementSet.addDocumentRequirement(copiedRequirement);
		}

		return DocumentRequirementSetMapper.toResponse(requirementSetRepository.save(requirementSet));
	}

	private DocumentRequirement copyDocumentRequirement(
			PresetDocumentRequirement presetRequirement,
			User ownerInstructor) {
		DocumentRequirement copiedRequirement = new DocumentRequirement(
				presetRequirement.getName(),
				presetRequirement.getSortOrder());
		copiedRequirement.setDescription(presetRequirement.getDescription());
		copiedRequirement.setRequired(presetRequirement.isRequired());
		copiedRequirement.setAllowedFileTypes(new LinkedHashSet<>(presetRequirement.getAllowedFileTypes()));
		copiedRequirement.setTemplate(copyTemplate(presetRequirement.getTemplate(), ownerInstructor));
		copiedRequirement.setRubric(copyRubric(presetRequirement.getRubric(), ownerInstructor));
		return copiedRequirement;
	}

	private DocumentTemplate copyTemplate(DocumentTemplate template, User ownerInstructor) {
		if (template == null) {
			return null;
		}

		DocumentTemplate copiedTemplate = new DocumentTemplate(template.getName(), ConfigurationStatus.DRAFT);
		copiedTemplate.setDescription(template.getDescription());
		copiedTemplate.setSourcePresetTemplateId(template.getId());
		copiedTemplate.setOwnerInstructor(ownerInstructor);

		for (TemplateSection section : template.getSections()) {
			TemplateSection copiedSection = new TemplateSection(section.getTitle(), section.getSortOrder());
			copiedSection.setDescription(section.getDescription());
			copiedSection.setRequired(section.isRequired());
			copiedSection.setMinimumWordCount(section.getMinimumWordCount());
			copiedTemplate.addSection(copiedSection);
		}

		return templateRepository.save(copiedTemplate);
	}

	private EvaluationRubric copyRubric(EvaluationRubric rubric, User ownerInstructor) {
		if (rubric == null) {
			return null;
		}

		EvaluationRubric copiedRubric = new EvaluationRubric(
				rubric.getName(),
				rubric.getTotalPoints(),
				rubric.getScoringType(),
				ConfigurationStatus.DRAFT);
		copiedRubric.setDescription(rubric.getDescription());
		copiedRubric.setSourcePresetRubricId(rubric.getId());
		copiedRubric.setOwnerInstructor(ownerInstructor);

		for (RubricCriterion criterion : rubric.getCriteria()) {
			RubricCriterion copiedCriterion = new RubricCriterion(
					criterion.getName(),
					criterion.getMaxPoints(),
					criterion.getSortOrder());
			copiedCriterion.setDescription(criterion.getDescription());
			copiedCriterion.setWeight(criterion.getWeight());
			for (RubricLevel level : criterion.getLevels()) {
				RubricLevel copiedLevel = new RubricLevel(
						level.getLevelName(),
						level.getPoints(),
						level.getSortOrder());
				copiedLevel.setDescription(level.getDescription());
				copiedCriterion.addLevel(copiedLevel);
			}
			copiedRubric.addCriterion(copiedCriterion);
		}

		return rubricRepository.save(copiedRubric);
	}

	private DocumentRequirementPreset findPreset(Long id) {
		return presetRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document requirement preset not found"));
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private boolean canViewPreset(User user, DocumentRequirementPreset preset) {
		if (user.getRole() == Role.ADMIN) {
			return true;
		}
		return user.getRole() == Role.INSTRUCTOR
				&& preset.getStatus() == ConfigurationStatus.ACTIVE
				&& preset.getVisibility() != PresetVisibility.INSTRUCTOR_PRIVATE;
	}
}
