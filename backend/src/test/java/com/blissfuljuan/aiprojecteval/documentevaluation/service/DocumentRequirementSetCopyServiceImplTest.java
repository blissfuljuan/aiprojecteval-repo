package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CopyPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RubricScoringType;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentTemplate;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.EvaluationRubric;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.PresetDocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricCriterion;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricLevel;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.TemplateSection;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementPreset;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementPresetRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentTemplateRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.EvaluationRubricRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentRequirementSetCopyServiceImplTest {

	@Mock
	private DocumentRequirementPresetRepository presetRepository;

	@Mock
	private DocumentRequirementSetRepository requirementSetRepository;

	@Mock
	private DocumentTemplateRepository templateRepository;

	@Mock
	private EvaluationRubricRepository rubricRepository;

	@Mock
	private UserRepository userRepository;

	private DocumentRequirementSetCopyServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new DocumentRequirementSetCopyServiceImpl(
				presetRepository,
				requirementSetRepository,
				templateRepository,
				rubricRepository,
				userRepository);
	}

	@Test
	void shouldCopyPresetIntoIndependentRequirementSet() {
		User instructor = user(10L, Role.INSTRUCTOR);
		DocumentRequirementPreset preset = preset(1L, ConfigurationStatus.ACTIVE);
		PresetDocumentRequirement presetRequirement = presetRequirementWithTemplateAndRubric();
		preset.addDocumentRequirement(presetRequirement);

		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(presetRepository.findById(1L)).thenReturn(Optional.of(preset));
		when(templateRepository.save(any(DocumentTemplate.class)))
				.thenAnswer(invocation -> {
					DocumentTemplate template = invocation.getArgument(0);
					template.setId(201L);
					return template;
				});
		when(rubricRepository.save(any(EvaluationRubric.class)))
				.thenAnswer(invocation -> {
					EvaluationRubric rubric = invocation.getArgument(0);
					rubric.setId(301L);
					return rubric;
				});
		when(requirementSetRepository.save(any(DocumentRequirementSet.class)))
				.thenAnswer(invocation -> {
					DocumentRequirementSet requirementSet = invocation.getArgument(0);
					requirementSet.setId(101L);
					return requirementSet;
				});

		DocumentRequirementSetResponse response = service.copyPresetToRequirementSet(
				"instructor@example.com",
				1L,
				new CopyPresetRequest(
						"IT342 Final Project Documentation Requirements",
						"Customized document requirements"));

		assertThat(response.id()).isEqualTo(101L);
		assertThat(response.name()).isEqualTo("IT342 Final Project Documentation Requirements");
		assertThat(response.description()).isEqualTo("Customized document requirements");
		assertThat(response.sourcePresetId()).isEqualTo(1L);
		assertThat(response.ownerInstructorId()).isEqualTo(10L);
		assertThat(response.status()).isEqualTo(ConfigurationStatus.DRAFT);
		assertThat(response.documentRequirements()).hasSize(1);

		var copiedRequirementResponse = response.documentRequirements().get(0);
		assertThat(copiedRequirementResponse.name()).isEqualTo("Project Documentation");
		assertThat(copiedRequirementResponse.description()).isEqualTo("Core document");
		assertThat(copiedRequirementResponse.required()).isFalse();
		assertThat(copiedRequirementResponse.allowedFileTypes()).containsExactly(AllowedFileType.PDF, AllowedFileType.DOCX);
		assertThat(copiedRequirementResponse.sortOrder()).isEqualTo(2);

		assertThat(copiedRequirementResponse.template().id()).isEqualTo(201L);
		assertThat(copiedRequirementResponse.template().sourcePresetTemplateId()).isEqualTo(20L);
		assertThat(copiedRequirementResponse.template().status()).isEqualTo(ConfigurationStatus.DRAFT);
		assertThat(copiedRequirementResponse.template().ownerInstructorId()).isEqualTo(10L);
		assertThat(copiedRequirementResponse.template().sections()).hasSize(1);
		assertThat(copiedRequirementResponse.template().sections().get(0).title()).isEqualTo("Introduction");
		assertThat(copiedRequirementResponse.template().sections().get(0).minimumWordCount()).isEqualTo(250);

		assertThat(copiedRequirementResponse.rubric().id()).isEqualTo(301L);
		assertThat(copiedRequirementResponse.rubric().sourcePresetRubricId()).isEqualTo(30L);
		assertThat(copiedRequirementResponse.rubric().status()).isEqualTo(ConfigurationStatus.DRAFT);
		assertThat(copiedRequirementResponse.rubric().ownerInstructorId()).isEqualTo(10L);
		assertThat(copiedRequirementResponse.rubric().criteria()).hasSize(1);
		assertThat(copiedRequirementResponse.rubric().criteria().get(0).name()).isEqualTo("Completeness");
		assertThat(copiedRequirementResponse.rubric().criteria().get(0).levels()).hasSize(1);
		assertThat(copiedRequirementResponse.rubric().criteria().get(0).levels().get(0).levelName()).isEqualTo("Excellent");

		ArgumentCaptor<DocumentRequirementSet> requirementSetCaptor =
				ArgumentCaptor.forClass(DocumentRequirementSet.class);
		verify(requirementSetRepository).save(requirementSetCaptor.capture());
		DocumentRequirementSet copiedRequirementSet = requirementSetCaptor.getValue();
		assertThat(copiedRequirementSet.getDocumentRequirements().get(0)).isNotSameAs(presetRequirement);
		assertThat(copiedRequirementSet.getDocumentRequirements().get(0).getTemplate())
				.isNotSameAs(presetRequirement.getTemplate());
		assertThat(copiedRequirementSet.getDocumentRequirements().get(0).getRubric())
				.isNotSameAs(presetRequirement.getRubric());
		assertThat(presetRequirement.getTemplate().getSourcePresetTemplateId()).isNull();
		assertThat(presetRequirement.getRubric().getSourcePresetRubricId()).isNull();
	}

	@Test
	void shouldFailWhenPresetDoesNotExist() {
		User instructor = user(10L, Role.INSTRUCTOR);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(presetRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.copyPresetToRequirementSet(
				"instructor@example.com",
				99L,
				new CopyPresetRequest("Requirement Set", null)))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Document requirement preset not found");

		verify(requirementSetRepository, never()).save(any(DocumentRequirementSet.class));
	}

	@Test
	void shouldFailWhenPresetIsArchived() {
		User admin = user(1L, Role.ADMIN);
		DocumentRequirementPreset preset = preset(1L, ConfigurationStatus.ARCHIVED);
		when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
		when(presetRepository.findById(1L)).thenReturn(Optional.of(preset));

		assertThatThrownBy(() -> service.copyPresetToRequirementSet(
				"admin@example.com",
				1L,
				new CopyPresetRequest("Requirement Set", null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Archived presets cannot be copied");

		verify(requirementSetRepository, never()).save(any(DocumentRequirementSet.class));
	}

	private User user(Long id, Role role) {
		User user = new User("Test", null, "User", "instructor@example.com", "encoded-password", role);
		user.setId(id);
		return user;
	}

	private DocumentRequirementPreset preset(Long id, ConfigurationStatus status) {
		DocumentRequirementPreset preset = new DocumentRequirementPreset(
				"Capstone Documents",
				PresetVisibility.SYSTEM,
				status);
		preset.setId(id);
		return preset;
	}

	private PresetDocumentRequirement presetRequirementWithTemplateAndRubric() {
		PresetDocumentRequirement requirement = new PresetDocumentRequirement("Project Documentation", 2);
		requirement.setDescription("Core document");
		requirement.setRequired(false);
		requirement.setAllowedFileTypes(new LinkedHashSet<>(List.of(AllowedFileType.PDF, AllowedFileType.DOCX)));
		requirement.setTemplate(template());
		requirement.setRubric(rubric());
		return requirement;
	}

	private DocumentTemplate template() {
		DocumentTemplate template = new DocumentTemplate("Final Paper Template", ConfigurationStatus.ACTIVE);
		template.setId(20L);
		template.setDescription("Template description");

		TemplateSection section = new TemplateSection("Introduction", 0);
		section.setDescription("Intro description");
		section.setRequired(true);
		section.setMinimumWordCount(250);
		template.addSection(section);

		return template;
	}

	private EvaluationRubric rubric() {
		EvaluationRubric rubric = new EvaluationRubric(
				"Final Paper Rubric",
				new BigDecimal("100.00"),
				RubricScoringType.POINTS,
				ConfigurationStatus.ACTIVE);
		rubric.setId(30L);
		rubric.setDescription("Rubric description");

		RubricCriterion criterion = new RubricCriterion("Completeness", new BigDecimal("50.00"), 0);
		criterion.setDescription("Criterion description");
		criterion.setWeight(new BigDecimal("0.50"));

		RubricLevel level = new RubricLevel("Excellent", new BigDecimal("50.00"), 0);
		level.setDescription("Complete and clear");
		criterion.addLevel(level);
		rubric.addCriterion(criterion);

		return rubric;
	}
}
