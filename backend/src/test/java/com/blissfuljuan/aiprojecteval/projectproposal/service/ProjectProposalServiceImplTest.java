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
import com.blissfuljuan.aiprojecteval.courseclass.service.CourseClassService;
import com.blissfuljuan.aiprojecteval.document.service.DocumentService;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.service.AuthService;
import com.blissfuljuan.aiprojecteval.project.service.ProjectService;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectProposalServiceImplTest {

	@Mock
	private ProjectProposalRepository projectProposalRepository;

	@Mock
	private CourseClassService courseClassService;

	@Mock
	private AuthService authService;

	@Mock
	private ProjectService projectService;

	@Mock
	private DocumentService documentService;

	private ProjectProposalServiceImpl projectProposalService;

	@BeforeEach
	void setUp() {
		projectProposalService = new ProjectProposalServiceImpl(
				projectProposalRepository,
				courseClassService,
				authService,
				projectService,
				documentService);
		lenient().when(documentService.findByContext(any(), any())).thenReturn(java.util.List.of());
	}

	@Test
	void shouldCreateSubmittedProposalForStudent() {
		User student = user(Role.STUDENT);
		CourseClass courseClass = courseClass();
		when(authService.getUserByEmail("student@example.com")).thenReturn(student);
		when(courseClassService.getCourseClassEntity(1L)).thenReturn(courseClass);
		when(courseClassService.isStudentEnrolled(1L, 1L)).thenReturn(true);
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
		when(authService.getUserByEmail("student@example.com")).thenReturn(user(Role.STUDENT));
		when(courseClassService.getCourseClassEntity(1L)).thenReturn(courseClass());
		when(courseClassService.isStudentEnrolled(1L, 1L)).thenReturn(true);
		when(projectProposalRepository.existsBySubmittedByIdAndStatusIn(any(), any())).thenReturn(true);

		assertThatThrownBy(() -> projectProposalService.createProposal("student@example.com", createRequest()))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("User already has an active project proposal");
	}

	@Test
	void shouldRejectStudentProposalForUnenrolledCourseClass() {
		when(authService.getUserByEmail("student@example.com")).thenReturn(user(Role.STUDENT));
		when(courseClassService.getCourseClassEntity(1L)).thenReturn(courseClass());
		when(courseClassService.isStudentEnrolled(1L, 1L)).thenReturn(false);

		assertThatThrownBy(() -> projectProposalService.createProposal("student@example.com", createRequest()))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Students can submit proposals only for enrolled course classes");
	}

	@Test
	void shouldUpdateSubmittedProposalByOwner() {
		User student = user(Role.STUDENT);
		ProjectProposal proposal = proposal(student, ProposalStatus.SUBMITTED);
		when(authService.getUserByEmail("student@example.com")).thenReturn(student);
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
		when(authService.getUserByEmail("student@example.com")).thenReturn(student);
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
		when(authService.getUserByEmail("instructor@example.com")).thenReturn(instructor);
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));
		when(projectService.existsByProjectProposalId(10L)).thenReturn(false);
		when(projectProposalRepository.save(proposal)).thenReturn(proposal);

		ProjectProposalResponse response = projectProposalService.instructorDecision(
				"instructor@example.com",
				10L,
				new ProposalDecisionRequest(ProposalStatus.APPROVED, "Approved"));

		assertThat(response.status()).isEqualTo(ProposalStatus.APPROVED);
		assertThat(response.approvedAt()).isNotNull();
		verify(projectService).createFromApprovedProposal(
				1L,
				"student@example.com",
				"Capstone Portal",
				"Expected output",
				10L);
	}

	@Test
	void shouldMarkProposalAsRevisionRequired() {
		User instructor = user(Role.INSTRUCTOR);
		ProjectProposal proposal = proposal(user(Role.STUDENT), ProposalStatus.SUBMITTED);
		when(authService.getUserByEmail("instructor@example.com")).thenReturn(instructor);
		when(projectProposalRepository.findById(10L)).thenReturn(Optional.of(proposal));
		when(projectProposalRepository.save(proposal)).thenReturn(proposal);

		ProjectProposalResponse response = projectProposalService.instructorDecision(
				"instructor@example.com",
				10L,
				new ProposalDecisionRequest(ProposalStatus.REVISION_REQUIRED, "Revise scope"));

		assertThat(response.status()).isEqualTo(ProposalStatus.REVISION_REQUIRED);
		assertThat(response.revisionRequestedAt()).isNotNull();
		verify(projectService, never()).createFromApprovedProposal(any(), any(), any(), any(), any());
	}

	@Test
	void shouldRejectProposal() {
		User instructor = user(Role.INSTRUCTOR);
		ProjectProposal proposal = proposal(user(Role.STUDENT), ProposalStatus.SUBMITTED);
		when(authService.getUserByEmail("instructor@example.com")).thenReturn(instructor);
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
		when(authService.getUserByEmail("student@example.com")).thenReturn(user(Role.STUDENT));

		assertThatThrownBy(() -> projectProposalService.instructorDecision(
				"student@example.com",
				10L,
				new ProposalDecisionRequest(ProposalStatus.APPROVED, "Approved")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only instructors or admins can make a proposal decision");
	}

	@Test
	void shouldRejectInvalidDecisionStatus() {
		when(authService.getUserByEmail("instructor@example.com")).thenReturn(user(Role.INSTRUCTOR));

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
		when(authService.getUserByEmail("instructor@example.com")).thenReturn(instructor);
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
		when(authService.getUserByEmail("adviser@example.com")).thenReturn(adviser);
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
		when(authService.getUserByEmail("adviser@example.com")).thenReturn(adviser);
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
		when(authService.getUserByEmail("student@example.com")).thenReturn(user(Role.STUDENT));

		assertThatThrownBy(() -> projectProposalService.adviserDecision(
				"student@example.com",
				10L,
				new AdviserDecisionRequest(ProposalStatus.ADVISER_REVIEWED, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only advisers or admins can record an adviser decision");
	}

	@Test
	void shouldRejectInvalidAdviserDecisionStatus() {
		when(authService.getUserByEmail("adviser@example.com")).thenReturn(user(Role.ADVISER));

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
		when(authService.getUserByEmail("adviser@example.com")).thenReturn(adviser);
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
