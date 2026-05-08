package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AddDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateRequirementSetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReorderDocumentRequirementItemRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReorderDocumentRequirementsRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateRequirementSetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RubricScoringType;
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
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class DocumentRequirementSetServiceImplTest {

	@Mock
	private DocumentRequirementSetRepository requirementSetRepository;

	@Mock
	private DocumentRequirementRepository requirementRepository;

	@Mock
	private DocumentTemplateRepository templateRepository;

	@Mock
	private EvaluationRubricRepository rubricRepository;

	@Mock
	private UserRepository userRepository;

	private DocumentRequirementSetServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new DocumentRequirementSetServiceImpl(
				requirementSetRepository,
				requirementRepository,
				templateRepository,
				rubricRepository,
				userRepository);
	}

	@Test
	void shouldCreateManualRequirementSetAsDraft() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.save(any(DocumentRequirementSet.class)))
				.thenAnswer(invocation -> {
					DocumentRequirementSet requirementSet = invocation.getArgument(0);
					requirementSet.setId(100L);
					return requirementSet;
				});

		DocumentRequirementSetResponse response = service.createRequirementSet(
				"instructor@example.com",
				new CreateRequirementSetRequest("Final Project Requirements", "Submission package"));

		assertThat(response.id()).isEqualTo(100L);
		assertThat(response.name()).isEqualTo("Final Project Requirements");
		assertThat(response.description()).isEqualTo("Submission package");
		assertThat(response.sourcePresetId()).isNull();
		assertThat(response.ownerInstructorId()).isEqualTo(10L);
		assertThat(response.status()).isEqualTo(ConfigurationStatus.DRAFT);
		assertThat(response.documentRequirements()).isEmpty();
	}

	@Test
	void shouldReturnRequirementSetsOwnedByInstructorWithFilters() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet matchingSet = requirementSet(100L, instructor, ConfigurationStatus.DRAFT);
		matchingSet.setName("Final Project Requirements");
		DocumentRequirementSet otherStatusSet = requirementSet(101L, instructor, ConfigurationStatus.ACTIVE);
		otherStatusSet.setName("Final Defense Requirements");

		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findByOwnerInstructorId(10L))
				.thenReturn(List.of(matchingSet, otherStatusSet));

		var responses = service.getMyRequirementSets("instructor@example.com", ConfigurationStatus.DRAFT, "final");

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).id()).isEqualTo(100L);
		assertThat(responses.get(0).documentRequirementCount()).isZero();
	}

	@Test
	void shouldUpdateRequirementSetMetadata() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(requirementSetRepository.save(any(DocumentRequirementSet.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementSetResponse response = service.updateRequirementSet(
				"instructor@example.com",
				100L,
				new UpdateRequirementSetRequest("Updated Requirements", "Updated description"));

		assertThat(response.name()).isEqualTo("Updated Requirements");
		assertThat(response.description()).isEqualTo("Updated description");
		assertThat(response.sourcePresetId()).isNull();
	}

	@Test
	void shouldPreventUpdatingArchivedRequirementSet() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.ARCHIVED);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));

		assertThatThrownBy(() -> service.updateRequirementSet(
				"instructor@example.com",
				100L,
				new UpdateRequirementSetRequest("Updated Requirements", null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Archived requirement sets cannot be modified");

		verify(requirementSetRepository, never()).save(any(DocumentRequirementSet.class));
	}

	@Test
	void shouldActivateRequirementSetWithAtLeastOneDocumentRequirement() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.DRAFT);
		requirementSet.addDocumentRequirement(requirement(200L, "Proposal", 0));
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(requirementSetRepository.save(any(DocumentRequirementSet.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementSetResponse response = service.activateRequirementSet("instructor@example.com", 100L);

		assertThat(response.status()).isEqualTo(ConfigurationStatus.ACTIVE);
	}

	@Test
	void shouldRejectActivationWithoutDocumentRequirements() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.DRAFT);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));

		assertThatThrownBy(() -> service.activateRequirementSet("instructor@example.com", 100L))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Requirement set must have at least one document requirement before activation");
	}

	@Test
	void shouldAddDocumentRequirementWithNextSortOrderAndReferences() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.DRAFT);
		requirementSet.addDocumentRequirement(requirement(200L, "Existing", 3));
		DocumentTemplate template = template(300L);
		EvaluationRubric rubric = rubric(400L);

		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(templateRepository.findById(300L)).thenReturn(Optional.of(template));
		when(rubricRepository.findById(400L)).thenReturn(Optional.of(rubric));
		when(requirementSetRepository.save(any(DocumentRequirementSet.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementResponse response = service.addDocumentRequirement(
				"instructor@example.com",
				100L,
				new AddDocumentRequirementRequest(
						"Project Proposal",
						"Initial proposal",
						null,
						List.of(AllowedFileType.PDF, AllowedFileType.DOCX),
						null,
						300L,
						400L));

		assertThat(response.name()).isEqualTo("Project Proposal");
		assertThat(response.required()).isTrue();
		assertThat(response.allowedFileTypes()).containsExactly(AllowedFileType.PDF, AllowedFileType.DOCX);
		assertThat(response.sortOrder()).isEqualTo(4);
		assertThat(response.template().id()).isEqualTo(300L);
		assertThat(response.rubric().id()).isEqualTo(400L);
	}

	@Test
	void shouldUpdateDocumentRequirementAndClearReferences() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.DRAFT);
		DocumentRequirement requirement = requirement(200L, "Proposal", 0);
		requirement.setTemplate(template(300L));
		requirement.setRubric(rubric(400L));
		requirementSet.addDocumentRequirement(requirement);

		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementRepository.findById(200L)).thenReturn(Optional.of(requirement));
		when(requirementRepository.save(any(DocumentRequirement.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementResponse response = service.updateDocumentRequirement(
				"instructor@example.com",
				200L,
				new UpdateDocumentRequirementRequest(
						"Updated Proposal",
						"Updated instructions",
						false,
						List.of(AllowedFileType.PDF),
						2,
						null,
						null));

		assertThat(response.name()).isEqualTo("Updated Proposal");
		assertThat(response.required()).isFalse();
		assertThat(response.allowedFileTypes()).containsExactly(AllowedFileType.PDF);
		assertThat(response.sortOrder()).isEqualTo(2);
		assertThat(response.template()).isNull();
		assertThat(response.rubric()).isNull();
	}

	@Test
	void shouldDeleteDocumentRequirementThroughParentAggregate() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.DRAFT);
		DocumentRequirement requirement = requirement(200L, "Proposal", 0);
		requirementSet.addDocumentRequirement(requirement);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementRepository.findById(200L)).thenReturn(Optional.of(requirement));
		when(requirementSetRepository.save(any(DocumentRequirementSet.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		service.deleteDocumentRequirement("instructor@example.com", 200L);

		assertThat(requirementSet.getDocumentRequirements()).isEmpty();
		assertThat(requirement.getRequirementSet()).isNull();
		verify(requirementSetRepository).save(requirementSet);
	}

	@Test
	void shouldReorderDocumentRequirements() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.DRAFT);
		requirementSet.addDocumentRequirement(requirement(200L, "Proposal", 0));
		requirementSet.addDocumentRequirement(requirement(201L, "Final Paper", 1));
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(requirementSetRepository.save(any(DocumentRequirementSet.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementSetResponse response = service.reorderDocumentRequirements(
				"instructor@example.com",
				100L,
				new ReorderDocumentRequirementsRequest(List.of(
						new ReorderDocumentRequirementItemRequest(200L, 2),
						new ReorderDocumentRequirementItemRequest(201L, 1))));

		assertThat(response.documentRequirements())
				.extracting(DocumentRequirementResponse::id)
				.containsExactly(201L, 200L);
	}

	@Test
	void shouldRejectReorderWhenRequirementDoesNotBelongToSet() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.DRAFT);
		requirementSet.addDocumentRequirement(requirement(200L, "Proposal", 0));
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));

		assertThatThrownBy(() -> service.reorderDocumentRequirements(
				"instructor@example.com",
				100L,
				new ReorderDocumentRequirementsRequest(List.of(
						new ReorderDocumentRequirementItemRequest(999L, 1)))))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("All document requirements must belong to the selected requirement set");
	}

	@Test
	void shouldRejectUnauthorizedInstructorAccess() {
		User owner = user(10L, "owner@example.com", Role.INSTRUCTOR);
		User otherInstructor = user(11L, "other@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, owner, ConfigurationStatus.DRAFT);
		when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherInstructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));

		assertThatThrownBy(() -> service.getRequirementSetById("other@example.com", 100L))
				.isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void shouldArchiveRequirementSet() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(requirementSetRepository.save(any(DocumentRequirementSet.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementSetResponse response = service.archiveRequirementSet("instructor@example.com", 100L);

		assertThat(response.status()).isEqualTo(ConfigurationStatus.ARCHIVED);
	}

	private User user(Long id, String email, Role role) {
		User user = new User("Test", null, "User", email, "encoded-password", role);
		user.setId(id);
		return user;
	}

	private DocumentRequirementSet requirementSet(Long id, User owner, ConfigurationStatus status) {
		DocumentRequirementSet requirementSet = new DocumentRequirementSet("Requirement Set", status);
		requirementSet.setId(id);
		requirementSet.setOwnerInstructor(owner);
		return requirementSet;
	}

	private DocumentRequirement requirement(Long id, String name, Integer sortOrder) {
		DocumentRequirement requirement = new DocumentRequirement(name, sortOrder);
		requirement.setId(id);
		requirement.setDescription("Requirement description");
		requirement.setRequired(true);
		requirement.setAllowedFileTypes(new LinkedHashSet<>(List.of(AllowedFileType.PDF)));
		return requirement;
	}

	private DocumentTemplate template(Long id) {
		DocumentTemplate template = new DocumentTemplate("Template", ConfigurationStatus.ACTIVE);
		template.setId(id);
		return template;
	}

	private EvaluationRubric rubric(Long id) {
		EvaluationRubric rubric = new EvaluationRubric(
				"Rubric",
				new BigDecimal("100.00"),
				RubricScoringType.POINTS,
				ConfigurationStatus.ACTIVE);
		rubric.setId(id);
		return rubric;
	}
}
