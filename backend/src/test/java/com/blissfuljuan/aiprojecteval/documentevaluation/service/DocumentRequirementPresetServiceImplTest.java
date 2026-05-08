package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentRequirementPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.PresetDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementPresetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementPreset;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementPresetRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentTemplateRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.EvaluationRubricRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.PresetDocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentRequirementPresetServiceImplTest {

	@Mock
	private DocumentRequirementPresetRepository presetRepository;

	@Mock
	private PresetDocumentRequirementRepository requirementRepository;

	@Mock
	private DocumentTemplateRepository templateRepository;

	@Mock
	private EvaluationRubricRepository rubricRepository;

	@Mock
	private UserRepository userRepository;

	private DocumentRequirementPresetServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new DocumentRequirementPresetServiceImpl(
				presetRepository,
				requirementRepository,
				templateRepository,
				rubricRepository,
				userRepository);
	}

	@Test
	void shouldCreatePresetWithDraftStatusByDefault() {
		User admin = user(1L, Role.ADMIN);
		when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
		when(presetRepository.save(any(DocumentRequirementPreset.class)))
				.thenAnswer(invocation -> {
					DocumentRequirementPreset preset = invocation.getArgument(0);
					preset.setId(1L);
					return preset;
				});

		DocumentRequirementPresetResponse response = service.createPreset(
				"admin@example.com",
				new CreateDocumentRequirementPresetRequest(
						"Capstone Documents",
						"Reusable capstone document requirements",
						"Capstone",
						PresetVisibility.SYSTEM,
						null));

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.status()).isEqualTo(ConfigurationStatus.DRAFT);
		assertThat(response.createdById()).isEqualTo(1L);
	}

	@Test
	void shouldAddRequirementWithDefaultAllowedFileTypes() {
		DocumentRequirementPreset preset = preset(1L);
		when(presetRepository.findById(1L)).thenReturn(Optional.of(preset));
		when(presetRepository.save(any(DocumentRequirementPreset.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementPresetResponse response = service.addDocumentRequirement(
				1L,
				new PresetDocumentRequirementRequest(
						"Requirements Package",
						"Core project documentation",
						null,
						List.of(),
						0,
						null,
						null));

		assertThat(response.documentRequirements()).hasSize(1);
		assertThat(response.documentRequirements().get(0).required()).isTrue();
		assertThat(response.documentRequirements().get(0).allowedFileTypes())
				.containsExactly(AllowedFileType.PDF, AllowedFileType.DOCX);
	}

	@Test
	void shouldArchiveAndActivatePreset() {
		DocumentRequirementPreset preset = preset(1L);
		when(presetRepository.findById(1L)).thenReturn(Optional.of(preset));
		when(presetRepository.save(any(DocumentRequirementPreset.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementPresetResponse archived = service.archivePreset(1L);
		DocumentRequirementPresetResponse activated = service.activatePreset(1L);

		assertThat(archived.status()).isEqualTo(ConfigurationStatus.ARCHIVED);
		assertThat(activated.status()).isEqualTo(ConfigurationStatus.ACTIVE);
	}

	private User user(Long id, Role role) {
		User user = new User("Admin", null, "User", "admin@example.com", "encoded-password", role);
		user.setId(id);
		return user;
	}

	private DocumentRequirementPreset preset(Long id) {
		DocumentRequirementPreset preset = new DocumentRequirementPreset(
				"Capstone Documents",
				PresetVisibility.SYSTEM,
				ConfigurationStatus.DRAFT);
		preset.setId(id);
		return preset;
	}
}
