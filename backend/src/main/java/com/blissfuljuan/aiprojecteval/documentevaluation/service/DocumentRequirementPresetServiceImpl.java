package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentRequirementPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.PresetDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentRequirementPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdatePresetDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementPresetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
import com.blissfuljuan.aiprojecteval.documentevaluation.mapper.DocumentRequirementPresetMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementPreset;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentTemplate;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.EvaluationRubric;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.PresetDocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementPresetRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentTemplateRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.EvaluationRubricRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.PresetDocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentRequirementPresetServiceImpl implements DocumentRequirementPresetService {

	private static final List<AllowedFileType> DEFAULT_ALLOWED_FILE_TYPES =
			List.of(AllowedFileType.PDF, AllowedFileType.DOCX);

	private final DocumentRequirementPresetRepository presetRepository;
	private final PresetDocumentRequirementRepository requirementRepository;
	private final DocumentTemplateRepository templateRepository;
	private final EvaluationRubricRepository rubricRepository;
	private final UserRepository userRepository;

	DocumentRequirementPresetServiceImpl(
			DocumentRequirementPresetRepository presetRepository,
			PresetDocumentRequirementRepository requirementRepository,
			DocumentTemplateRepository templateRepository,
			EvaluationRubricRepository rubricRepository,
			UserRepository userRepository) {
		this.presetRepository = presetRepository;
		this.requirementRepository = requirementRepository;
		this.templateRepository = templateRepository;
		this.rubricRepository = rubricRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public DocumentRequirementPresetResponse createPreset(
			String currentUserEmail,
			CreateDocumentRequirementPresetRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementPreset preset = new DocumentRequirementPreset(
				request.name(),
				request.visibility(),
				request.status() == null ? ConfigurationStatus.DRAFT : request.status()
		);
		preset.setDescription(request.description());
		preset.setCategory(request.category());
		preset.setCreatedBy(currentUser);

		return DocumentRequirementPresetMapper.toResponse(presetRepository.save(preset));
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentRequirementPresetResponse getPresetById(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementPreset preset = findPreset(id);
		if (!canViewPreset(currentUser, preset)) {
			throw new ResourceNotFoundException("Document requirement preset not found");
		}

		return DocumentRequirementPresetMapper.toResponse(preset);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentRequirementPresetResponse> listPresets(
			String currentUserEmail,
			ConfigurationStatus status,
			PresetVisibility visibility,
			String category,
			String search) {
		User currentUser = findUserByEmail(currentUserEmail);
		String normalizedCategory = normalize(category);
		String normalizedSearch = normalize(search);

		return presetRepository.findAll()
				.stream()
				.filter(preset -> canViewPreset(currentUser, preset))
				.filter(preset -> status == null || preset.getStatus() == status)
				.filter(preset -> visibility == null || preset.getVisibility() == visibility)
				.filter(preset -> normalizedCategory == null || normalizedCategory.equals(normalize(preset.getCategory())))
				.filter(preset -> normalizedSearch == null
						|| containsIgnoreCase(preset.getName(), normalizedSearch)
						|| containsIgnoreCase(preset.getDescription(), normalizedSearch)
						|| containsIgnoreCase(preset.getCategory(), normalizedSearch))
				.map(DocumentRequirementPresetMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public DocumentRequirementPresetResponse updatePreset(Long id, UpdateDocumentRequirementPresetRequest request) {
		DocumentRequirementPreset preset = findPreset(id);
		preset.setName(request.name());
		preset.setDescription(request.description());
		preset.setCategory(request.category());
		preset.setVisibility(request.visibility());
		if (request.status() != null) {
			preset.setStatus(request.status());
		}

		return DocumentRequirementPresetMapper.toResponse(presetRepository.save(preset));
	}

	@Override
	@Transactional
	public DocumentRequirementPresetResponse archivePreset(Long id) {
		DocumentRequirementPreset preset = findPreset(id);
		preset.setStatus(ConfigurationStatus.ARCHIVED);
		return DocumentRequirementPresetMapper.toResponse(presetRepository.save(preset));
	}

	@Override
	@Transactional
	public DocumentRequirementPresetResponse activatePreset(Long id) {
		DocumentRequirementPreset preset = findPreset(id);
		preset.setStatus(ConfigurationStatus.ACTIVE);
		return DocumentRequirementPresetMapper.toResponse(presetRepository.save(preset));
	}

	@Override
	@Transactional
	public DocumentRequirementPresetResponse addDocumentRequirement(
			Long presetId,
			PresetDocumentRequirementRequest request) {
		DocumentRequirementPreset preset = findPreset(presetId);
		PresetDocumentRequirement requirement = new PresetDocumentRequirement(request.name(), request.sortOrder());
		requirement.setDescription(request.description());
		requirement.setRequired(request.required() == null || request.required());
		requirement.setAllowedFileTypes(resolveAllowedFileTypes(request.allowedFileTypes()));
		requirement.setTemplate(resolveTemplate(request.templateId()));
		requirement.setRubric(resolveRubric(request.rubricId()));
		preset.addDocumentRequirement(requirement);

		return DocumentRequirementPresetMapper.toResponse(presetRepository.save(preset));
	}

	@Override
	@Transactional
	public DocumentRequirementPresetResponse updateDocumentRequirement(
			Long presetId,
			Long requirementId,
			UpdatePresetDocumentRequirementRequest request) {
		DocumentRequirementPreset preset = findPreset(presetId);
		PresetDocumentRequirement requirement = findRequirementInPreset(preset, requirementId);
		requirement.setName(request.name());
		requirement.setDescription(request.description());
		if (request.required() != null) {
			requirement.setRequired(request.required());
		}
		if (request.allowedFileTypes() != null) {
			requirement.setAllowedFileTypes(resolveAllowedFileTypes(request.allowedFileTypes()));
		}
		requirement.setSortOrder(request.sortOrder());
		requirement.setTemplate(resolveTemplate(request.templateId()));
		requirement.setRubric(resolveRubric(request.rubricId()));

		return DocumentRequirementPresetMapper.toResponse(presetRepository.save(preset));
	}

	@Override
	@Transactional
	public void removeDocumentRequirement(Long presetId, Long requirementId) {
		DocumentRequirementPreset preset = findPreset(presetId);
		PresetDocumentRequirement requirement = findRequirementInPreset(preset, requirementId);
		preset.removeDocumentRequirement(requirement);
		presetRepository.save(preset);
	}

	private DocumentRequirementPreset findPreset(Long id) {
		return presetRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document requirement preset not found"));
	}

	private PresetDocumentRequirement findRequirementInPreset(
			DocumentRequirementPreset preset,
			Long requirementId) {
		return preset.getDocumentRequirements()
				.stream()
				.filter(requirement -> requirementId.equals(requirement.getId()))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Preset document requirement not found"));
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private DocumentTemplate resolveTemplate(Long templateId) {
		if (templateId == null) {
			return null;
		}
		return templateRepository.findById(templateId)
				.orElseThrow(() -> new ResourceNotFoundException("Document template not found"));
	}

	private EvaluationRubric resolveRubric(Long rubricId) {
		if (rubricId == null) {
			return null;
		}
		return rubricRepository.findById(rubricId)
				.orElseThrow(() -> new ResourceNotFoundException("Evaluation rubric not found"));
	}

	private Set<AllowedFileType> resolveAllowedFileTypes(List<AllowedFileType> allowedFileTypes) {
		if (allowedFileTypes == null || allowedFileTypes.isEmpty()) {
			return new LinkedHashSet<>(DEFAULT_ALLOWED_FILE_TYPES);
		}
		return new LinkedHashSet<>(allowedFileTypes);
	}

	private boolean canViewPreset(User user, DocumentRequirementPreset preset) {
		if (user.getRole() == Role.ADMIN) {
			return true;
		}
		return user.getRole() == Role.INSTRUCTOR
				&& preset.getStatus() == ConfigurationStatus.ACTIVE
				&& preset.getVisibility() != PresetVisibility.INSTRUCTOR_PRIVATE;
	}

	private String normalize(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim().toLowerCase(Locale.ROOT);
	}

	private boolean containsIgnoreCase(String value, String normalizedSearch) {
		return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedSearch);
	}
}
