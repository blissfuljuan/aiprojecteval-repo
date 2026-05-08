package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AddDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateRequirementSetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReorderDocumentRequirementItemRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReorderDocumentRequirementsRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateRequirementSetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.mapper.DocumentRequirementSetMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentTemplate;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.EvaluationRubric;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentTemplateRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.EvaluationRubricRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentRequirementSetServiceImpl implements DocumentRequirementSetService {

	private final DocumentRequirementSetRepository requirementSetRepository;
	private final DocumentRequirementRepository requirementRepository;
	private final DocumentTemplateRepository templateRepository;
	private final EvaluationRubricRepository rubricRepository;
	private final UserRepository userRepository;

	DocumentRequirementSetServiceImpl(
			DocumentRequirementSetRepository requirementSetRepository,
			DocumentRequirementRepository requirementRepository,
			DocumentTemplateRepository templateRepository,
			EvaluationRubricRepository rubricRepository,
			UserRepository userRepository) {
		this.requirementSetRepository = requirementSetRepository;
		this.requirementRepository = requirementRepository;
		this.templateRepository = templateRepository;
		this.rubricRepository = rubricRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public DocumentRequirementSetResponse createRequirementSet(
			String currentUserEmail,
			CreateRequirementSetRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSet requirementSet = new DocumentRequirementSet(
				request.name(),
				ConfigurationStatus.DRAFT);
		requirementSet.setDescription(request.description());
		requirementSet.setOwnerInstructor(currentUser);

		return DocumentRequirementSetMapper.toResponse(requirementSetRepository.save(requirementSet));
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentRequirementSetSummaryResponse> getMyRequirementSets(
			String currentUserEmail,
			ConfigurationStatus status,
			String keyword) {
		User currentUser = findUserByEmail(currentUserEmail);
		return filterRequirementSets(
				requirementSetRepository.findByOwnerInstructorId(currentUser.getId()),
				currentUser,
				status,
				currentUser.getId(),
				keyword);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentRequirementSetSummaryResponse> getRequirementSets(
			String currentUserEmail,
			ConfigurationStatus status,
			Long ownerInstructorId,
			String keyword) {
		User currentUser = findUserByEmail(currentUserEmail);
		Long effectiveOwnerInstructorId = currentUser.getRole() == Role.ADMIN
				? ownerInstructorId
				: currentUser.getId();

		return filterRequirementSets(
				requirementSetRepository.findAll(),
				currentUser,
				status,
				effectiveOwnerInstructorId,
				keyword);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentRequirementSetResponse getRequirementSetById(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSet requirementSet = findRequirementSet(id);
		checkCanView(currentUser, requirementSet);

		return DocumentRequirementSetMapper.toResponse(requirementSet);
	}

	@Override
	@Transactional
	public DocumentRequirementSetResponse updateRequirementSet(
			String currentUserEmail,
			Long id,
			UpdateRequirementSetRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSet requirementSet = findRequirementSet(id);
		checkCanModify(currentUser, requirementSet);
		checkEditable(requirementSet);

		requirementSet.setName(request.name());
		requirementSet.setDescription(request.description());

		return DocumentRequirementSetMapper.toResponse(requirementSetRepository.save(requirementSet));
	}

	@Override
	@Transactional
	public DocumentRequirementSetResponse activateRequirementSet(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSet requirementSet = findRequirementSet(id);
		checkCanModify(currentUser, requirementSet);
		if (requirementSet.getDocumentRequirements().isEmpty()) {
			throw new BadRequestException("Requirement set must have at least one document requirement before activation");
		}

		requirementSet.setStatus(ConfigurationStatus.ACTIVE);
		return DocumentRequirementSetMapper.toResponse(requirementSetRepository.save(requirementSet));
	}

	@Override
	@Transactional
	public DocumentRequirementSetResponse archiveRequirementSet(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSet requirementSet = findRequirementSet(id);
		checkCanModify(currentUser, requirementSet);

		requirementSet.setStatus(ConfigurationStatus.ARCHIVED);
		return DocumentRequirementSetMapper.toResponse(requirementSetRepository.save(requirementSet));
	}

	@Override
	@Transactional
	public DocumentRequirementResponse addDocumentRequirement(
			String currentUserEmail,
			Long requirementSetId,
			AddDocumentRequirementRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSet requirementSet = findRequirementSet(requirementSetId);
		checkCanModify(currentUser, requirementSet);
		checkEditable(requirementSet);

		DocumentRequirement requirement = new DocumentRequirement(
				request.name(),
				request.sortOrder() == null ? nextSortOrder(requirementSet) : request.sortOrder());
		requirement.setDescription(request.description());
		requirement.setRequired(request.required() == null || request.required());
		requirement.setAllowedFileTypes(resolveAllowedFileTypes(request.allowedFileTypes()));
		requirement.setTemplate(resolveTemplate(request.templateId()));
		requirement.setRubric(resolveRubric(request.rubricId()));
		requirementSet.addDocumentRequirement(requirement);
		requirementSetRepository.save(requirementSet);

		return DocumentRequirementSetMapper.toRequirementResponse(requirement);
	}

	@Override
	@Transactional
	public DocumentRequirementResponse updateDocumentRequirement(
			String currentUserEmail,
			Long requirementId,
			UpdateDocumentRequirementRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirement requirement = findRequirement(requirementId);
		DocumentRequirementSet requirementSet = requirement.getRequirementSet();
		checkCanModify(currentUser, requirementSet);
		checkEditable(requirementSet);

		requirement.setName(request.name());
		requirement.setDescription(request.description());
		if (request.required() != null) {
			requirement.setRequired(request.required());
		}
		requirement.setAllowedFileTypes(resolveAllowedFileTypes(request.allowedFileTypes()));
		requirement.setSortOrder(request.sortOrder());
		requirement.setTemplate(resolveTemplate(request.templateId()));
		requirement.setRubric(resolveRubric(request.rubricId()));

		return DocumentRequirementSetMapper.toRequirementResponse(requirementRepository.save(requirement));
	}

	@Override
	@Transactional
	public void deleteDocumentRequirement(String currentUserEmail, Long requirementId) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirement requirement = findRequirement(requirementId);
		DocumentRequirementSet requirementSet = requirement.getRequirementSet();
		checkCanModify(currentUser, requirementSet);
		checkEditable(requirementSet);

		requirementSet.removeDocumentRequirement(requirement);
		requirementSetRepository.save(requirementSet);
	}

	@Override
	@Transactional
	public DocumentRequirementSetResponse reorderDocumentRequirements(
			String currentUserEmail,
			Long requirementSetId,
			ReorderDocumentRequirementsRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSet requirementSet = findRequirementSet(requirementSetId);
		checkCanModify(currentUser, requirementSet);
		checkEditable(requirementSet);

		Map<Long, DocumentRequirement> requirementsById = new HashMap<>();
		for (DocumentRequirement requirement : requirementSet.getDocumentRequirements()) {
			requirementsById.put(requirement.getId(), requirement);
		}

		Set<Long> seenRequirementIds = new LinkedHashSet<>();
		for (ReorderDocumentRequirementItemRequest item : request.items()) {
			if (!seenRequirementIds.add(item.requirementId())) {
				throw new BadRequestException("Duplicate document requirement in reorder request");
			}
			DocumentRequirement requirement = requirementsById.get(item.requirementId());
			if (requirement == null) {
				throw new BadRequestException("All document requirements must belong to the selected requirement set");
			}
			requirement.setSortOrder(item.sortOrder());
		}

		return DocumentRequirementSetMapper.toResponse(requirementSetRepository.save(requirementSet));
	}

	private List<DocumentRequirementSetSummaryResponse> filterRequirementSets(
			List<DocumentRequirementSet> requirementSets,
			User currentUser,
			ConfigurationStatus status,
			Long ownerInstructorId,
			String keyword) {
		String normalizedKeyword = normalize(keyword);

		return requirementSets.stream()
				.filter(requirementSet -> canView(currentUser, requirementSet))
				.filter(requirementSet -> status == null || requirementSet.getStatus() == status)
				.filter(requirementSet -> ownerInstructorId == null
						|| hasOwner(requirementSet, ownerInstructorId))
				.filter(requirementSet -> normalizedKeyword == null
						|| containsIgnoreCase(requirementSet.getName(), normalizedKeyword)
						|| containsIgnoreCase(requirementSet.getDescription(), normalizedKeyword))
				.map(DocumentRequirementSetMapper::toSummaryResponse)
				.toList();
	}

	private DocumentRequirementSet findRequirementSet(Long id) {
		return requirementSetRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document requirement set not found"));
	}

	private DocumentRequirement findRequirement(Long id) {
		return requirementRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document requirement not found"));
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
			throw new BadRequestException("Allowed file types must not be empty");
		}
		return new LinkedHashSet<>(allowedFileTypes);
	}

	private void checkCanView(User user, DocumentRequirementSet requirementSet) {
		if (!canView(user, requirementSet)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	private void checkCanModify(User user, DocumentRequirementSet requirementSet) {
		if (user.getRole() == Role.ADMIN) {
			return;
		}
		if (user.getRole() == Role.INSTRUCTOR && hasOwner(requirementSet, user.getId())) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private boolean canView(User user, DocumentRequirementSet requirementSet) {
		return user.getRole() == Role.ADMIN
				|| (user.getRole() == Role.INSTRUCTOR && hasOwner(requirementSet, user.getId()));
	}

	private boolean hasOwner(DocumentRequirementSet requirementSet, Long ownerInstructorId) {
		return requirementSet.getOwnerInstructor() != null
				&& ownerInstructorId.equals(requirementSet.getOwnerInstructor().getId());
	}

	private void checkEditable(DocumentRequirementSet requirementSet) {
		if (requirementSet.getStatus() == ConfigurationStatus.ARCHIVED) {
			throw new BadRequestException("Archived requirement sets cannot be modified");
		}
	}

	private int nextSortOrder(DocumentRequirementSet requirementSet) {
		return requirementSet.getDocumentRequirements()
				.stream()
				.map(DocumentRequirement::getSortOrder)
				.max(Integer::compareTo)
				.map(sortOrder -> sortOrder + 1)
				.orElse(0);
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
