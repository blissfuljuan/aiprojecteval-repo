package com.blissfuljuan.aiprojecteval.projectproposal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import com.blissfuljuan.aiprojecteval.document.service.DocumentService;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.AdviserDecisionRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalCreateRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalResponse;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalUpdateRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProposalDecisionRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProjectProposal;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProposalStatus;
import com.blissfuljuan.aiprojecteval.projectproposal.repository.ProjectProposalRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectProposalServiceImplTest {

	@Mock
	private ProjectProposalRepository projectProposalRepository;

	@Mock
	private CourseClassRepository courseClassRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private DocumentService documentService;

	private ProjectProposalServiceImpl projectProposalService;

	@BeforeEach
	void setUp() {
		projectProposalService = new ProjectProposalServiceImpl(
				projectProposalRepository,
				courseClassRepository,
				userRepository,
				projectRepository,
				documentService);
		lenient().when(documentService.findByContext(any(), any())).thenReturn(java.util.List.of());
	}

	@Test
	void shouldCreateSubmittedProposalForStudent() {
		User student = user(Role.STUDENT);
		CourseClass courseClass = courseClass();
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(courseClassRepository.findById(1L)).thenReturn(Optional.of(courseClass));
		when(projectProposalRepository.existsBySubmittedByIdAndStatusIn(any(), any())).thenReturn(false);
		when(projectProposalRepository.save(any(ProjectProposal.class))).thenAnswer(invocation -> {
			ProjectProposal proposal = invocation.getArgument(0);
			proposal.setId(10L);
			proposal.setCreatedAt(LocalDateTime.now());
			proposal.setUpdatedAt(LocalDateTime.now());
			return proposal;
		});

		ProjectProposalResponse response = projectProposalService.createProposal("student@example.com", createRequest());

		assertThat(response.status()).isEqualTo(ProposalStatus.SUBMITTED);
		assertThat(response.submittedByUserId()).isEqualTo(1L);
		assertThat(response.courseClassId()).isEqualTo(1L);
	}

	@Test
	void shouldRejectDuplicateActiveProposalForSubmitter() {
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user(Role.STUDENT)));
		when(courseClassRepository.findById(1L)).thenReturn(Optional.of(courseClass()));
		when(projectProposalRepository.existsBySubmittedByIdAndStatusIn(any(), any())).thenReturn(true);

		assertThatThrownBy(() -> projectProposalService.createProposal("student@example.com", createRequest()))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("User already has an active project proposal");
	}

	@Test
	void shouldUpdateSubmittedProposalByOwner() {
		User student = user(Role.STUDENT);
		ProjectProposal proposal = proposal(student, ProposalStatus.SUBMITTED);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));
		when(projectProposalRepository.save(proposal)).thenReturn(proposal);

		ProjectProposalResponse response = projectProposalService.updateProposal(
				"student@example.com",
				10L,
				updateRequest("Updated title"));

		assertThat(response.title()).isEqualTo("Updated title");
	}

	@Test
	void shouldRejectUpdateForApprovedProposal() {
		User student = user(Role.STUDENT);
		ProjectProposal proposal = proposal(student, ProposalStatus.APPROVED);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));

		assertThatThrownBy(() -> projectProposalService.updateProposal("student@example.com", 10L, updateRequest("Updated")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Proposal cannot be updated in its current status");
	}

	@Test
	void shouldApproveProposalAndCreateProject() {
		User instructor = user(Role.INSTRUCTOR);
		User student = user(Role.STUDENT);
		ProjectProposal proposal = proposal(student, ProposalStatus.SUBMITTED);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));
		when(projectRepository.existsByProjectProposalId(10L)).thenReturn(false);
		when(projectProposalRepository.save(proposal)).thenReturn(proposal);

		ProjectProposalResponse response = projectProposalService.instructorDecision(
				"instructor@example.com",
				10L,
				new ProposalDecisionRequest(ProposalStatus.APPROVED, "Approved"));

		assertThat(response.status()).isEqualTo(ProposalStatus.APPROVED);
		assertThat(response.approvedAt()).isNotNull();
		ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
		verify(projectRepository).save(projectCaptor.capture());
		assertThat(projectCaptor.getValue().getProjectProposalId()).isEqualTo(10L);
		assertThat(projectCaptor.getValue().getTitle()).isEqualTo("Capstone Portal");
	}

	@Test
	void shouldMarkProposalAsRevisionRequired() {
		User instructor = user(Role.INSTRUCTOR);
		ProjectProposal proposal = proposal(user(Role.STUDENT), ProposalStatus.SUBMITTED);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));
		when(projectProposalRepository.save(proposal)).thenReturn(proposal);

		ProjectProposalResponse response = projectProposalService.instructorDecision(
				"instructor@example.com",
				10L,
				new ProposalDecisionRequest(ProposalStatus.REVISION_REQUIRED, "Revise scope"));

		assertThat(response.status()).isEqualTo(ProposalStatus.REVISION_REQUIRED);
		assertThat(response.revisionRequestedAt()).isNotNull();
		verify(projectRepository, never()).save(any(Project.class));
	}

	@Test
	void shouldRejectProposal() {
		User instructor = user(Role.INSTRUCTOR);
		ProjectProposal proposal = proposal(user(Role.STUDENT), ProposalStatus.SUBMITTED);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));
		when(projectProposalRepository.save(proposal)).thenReturn(proposal);

		ProjectProposalResponse response = projectProposalService.instructorDecision(
				"instructor@example.com",
				10L,
				new ProposalDecisionRequest(ProposalStatus.REJECTED, "Not feasible"));

		assertThat(response.status()).isEqualTo(ProposalStatus.REJECTED);
		assertThat(response.rejectedAt()).isNotNull();
	}

	@Test
	void shouldRejectStudentDecision() {
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user(Role.STUDENT)));

		assertThatThrownBy(() -> projectProposalService.instructorDecision(
				"student@example.com",
				10L,
				new ProposalDecisionRequest(ProposalStatus.APPROVED, "Approved")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only instructors or admins can make a proposal decision");
	}

	@Test
	void shouldRejectInvalidDecisionStatus() {
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(user(Role.INSTRUCTOR)));

		assertThatThrownBy(() -> projectProposalService.instructorDecision(
				"instructor@example.com",
				10L,
				new ProposalDecisionRequest(ProposalStatus.SUBMITTED, "Invalid")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Decision must be APPROVED, REVISION_REQUIRED, or REJECTED");
	}

	@Test
	void shouldRejectDecisionForAlreadyApprovedProposal() {
		User instructor = user(Role.INSTRUCTOR);
		ProjectProposal proposal = proposal(user(Role.STUDENT), ProposalStatus.APPROVED);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));

		assertThatThrownBy(() -> projectProposalService.instructorDecision(
				"instructor@example.com",
				10L,
				new ProposalDecisionRequest(ProposalStatus.REJECTED, "Reject later")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Proposal already has a final decision");
	}

	@Test
	void shouldScheduleAdviserReview() {
		User adviser = user(Role.ADVISER);
		ProjectProposal proposal = proposal(user(Role.STUDENT), ProposalStatus.SUBMITTED);
		when(userRepository.findByEmail("adviser@example.com")).thenReturn(Optional.of(adviser));
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));
		when(projectProposalRepository.save(proposal)).thenReturn(proposal);

		ProjectProposalResponse response = projectProposalService.adviserDecision(
				"adviser@example.com",
				10L,
				new AdviserDecisionRequest(ProposalStatus.ADVISER_REVIEW_SCHEDULED, null));

		assertThat(response.status()).isEqualTo(ProposalStatus.ADVISER_REVIEW_SCHEDULED);
	}

	@Test
	void shouldCompleteAdviserReviewWithRemarks() {
		User adviser = user(Role.ADVISER);
		ProjectProposal proposal = proposal(user(Role.STUDENT), ProposalStatus.ADVISER_REVIEW_SCHEDULED);
		when(userRepository.findByEmail("adviser@example.com")).thenReturn(Optional.of(adviser));
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));
		when(projectProposalRepository.save(proposal)).thenReturn(proposal);

		ProjectProposalResponse response = projectProposalService.adviserDecision(
				"adviser@example.com",
				10L,
				new AdviserDecisionRequest(ProposalStatus.ADVISER_REVIEWED, "Scope is feasible"));

		assertThat(response.status()).isEqualTo(ProposalStatus.ADVISER_REVIEWED);
		assertThat(proposal.getAdviserRemarks()).isEqualTo("Scope is feasible");
	}

	@Test
	void shouldRejectAdviserDecisionFromStudent() {
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user(Role.STUDENT)));

		assertThatThrownBy(() -> projectProposalService.adviserDecision(
				"student@example.com",
				10L,
				new AdviserDecisionRequest(ProposalStatus.ADVISER_REVIEWED, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only advisers or admins can record an adviser decision");
	}

	@Test
	void shouldRejectInvalidAdviserDecisionStatus() {
		when(userRepository.findByEmail("adviser@example.com")).thenReturn(Optional.of(user(Role.ADVISER)));

		assertThatThrownBy(() -> projectProposalService.adviserDecision(
				"adviser@example.com",
				10L,
				new AdviserDecisionRequest(ProposalStatus.APPROVED, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Decision must be ADVISER_REVIEW_SCHEDULED or ADVISER_REVIEWED");
	}

	@Test
	void shouldRejectAdviserDecisionOnApprovedProposal() {
		User adviser = user(Role.ADVISER);
		ProjectProposal proposal = proposal(user(Role.STUDENT), ProposalStatus.APPROVED);
		when(userRepository.findByEmail("adviser@example.com")).thenReturn(Optional.of(adviser));
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));

		assertThatThrownBy(() -> projectProposalService.adviserDecision(
				"adviser@example.com",
				10L,
				new AdviserDecisionRequest(ProposalStatus.ADVISER_REVIEWED, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Proposal cannot receive an adviser decision in its current status");
	}

	private ProjectProposalCreateRequest createRequest() {
		return new ProjectProposalCreateRequest(
				"Capstone Portal",
				"Problem statement",
				"Objectives",
				"Students",
				"Features",
				"Spring Boot",
				"Expected output",
				1L,
				null);
	}

	private ProjectProposalUpdateRequest updateRequest(String title) {
		return new ProjectProposalUpdateRequest(
				title,
				"Updated problem",
				"Updated objectives",
				"Students",
				"Updated features",
				"Spring Boot",
				"Updated output");
	}

	private ProjectProposal proposal(User submittedBy, ProposalStatus status) {
		ProjectProposal proposal = new ProjectProposal();
		proposal.setId(10L);
		proposal.setTitle("Capstone Portal");
		proposal.setProblemStatement("Problem statement");
		proposal.setObjectives("Objectives");
		proposal.setTargetUsers("Students");
		proposal.setProposedFeatures("Features");
		proposal.setTechnologyStack("Spring Boot");
		proposal.setExpectedOutput("Expected output");
		proposal.setStatus(status);
		proposal.setCourseClass(courseClass());
		proposal.setSubmittedBy(submittedBy);
		proposal.setCreatedAt(LocalDateTime.now());
		proposal.setUpdatedAt(LocalDateTime.now());
		return proposal;
	}

	private CourseClass courseClass() {
		CourseClass courseClass = new CourseClass("Capstone 1", "MCS101");
		courseClass.setId(1L);
		return courseClass;
	}

	private User user(Role role) {
		User user = new User("Test", null, "User", role.name().toLowerCase() + "@example.com", "password", role);
		user.setId(role == Role.STUDENT ? 1L : 2L);
		return user;
	}
}
