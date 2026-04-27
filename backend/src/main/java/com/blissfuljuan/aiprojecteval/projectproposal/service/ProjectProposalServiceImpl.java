package com.blissfuljuan.aiprojecteval.projectproposal.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalCreateRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalResponse;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalUpdateRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProposalDecisionRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProjectProposal;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProposalStatus;
import com.blissfuljuan.aiprojecteval.projectproposal.repository.ProjectProposalRepository;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ProjectProposalServiceImpl implements ProjectProposalService {

	private static final Set<ProposalStatus> ACTIVE_STATUSES = EnumSet.of(
			ProposalStatus.SUBMITTED,
			ProposalStatus.ADVISER_REVIEW_SCHEDULED,
			ProposalStatus.ADVISER_REVIEWED,
			ProposalStatus.INSTRUCTOR_REVIEW_SCHEDULED,
			ProposalStatus.APPROVED
	);
	private static final Set<ProposalStatus> EDITABLE_STATUSES = EnumSet.of(
			ProposalStatus.DRAFT,
			ProposalStatus.SUBMITTED,
			ProposalStatus.REVISION_REQUIRED
	);
	private static final Set<ProposalStatus> FINAL_DECISIONS = EnumSet.of(
			ProposalStatus.APPROVED,
			ProposalStatus.REVISION_REQUIRED,
			ProposalStatus.REJECTED
	);

	private final ProjectProposalRepository projectProposalRepository;
	private final CourseClassRepository courseClassRepository;
	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;

	ProjectProposalServiceImpl(
			ProjectProposalRepository projectProposalRepository,
			CourseClassRepository courseClassRepository,
			UserRepository userRepository,
			ProjectRepository projectRepository) {
		this.projectProposalRepository = projectProposalRepository;
		this.courseClassRepository = courseClassRepository;
		this.userRepository = userRepository;
		this.projectRepository = projectRepository;
	}

	@Override
	@Transactional
	public ProjectProposalResponse createProposal(String currentUserEmail, ProjectProposalCreateRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		CourseClass courseClass = courseClassRepository.findById(request.courseClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Course class not found"));

		if (projectProposalRepository.existsBySubmittedByIdAndStatusIn(currentUser.getId(), ACTIVE_STATUSES)) {
			throw new BadRequestException("User already has an active project proposal");
		}

		ProjectProposal proposal = new ProjectProposal();
		applyCreateRequest(proposal, request);
		proposal.setCourseClass(courseClass);
		proposal.setSubmittedBy(currentUser);
		proposal.setStatus(ProposalStatus.SUBMITTED);

		return ProjectProposalResponse.fromEntity(projectProposalRepository.save(proposal));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProjectProposalResponse> getMyProposals(String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);

		return projectProposalRepository.findBySubmittedById(currentUser.getId())
				.stream()
				.map(ProjectProposalResponse::fromEntity)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProjectProposalResponse> getAllProposals(String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		if (currentUser.getRole() == Role.STUDENT) {
			throw new BadRequestException("Students cannot view all proposals");
		}
		if (currentUser.getRole() == Role.EVALUATOR) {
			throw new BadRequestException("Evaluators cannot view all proposals");
		}

		// TODO: Restrict instructors to assigned classes and advisers to scheduled consultations when those modules exist.
		return projectProposalRepository.findAll()
				.stream()
				.map(ProjectProposalResponse::fromEntity)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProjectProposalResponse getProposalById(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		ProjectProposal proposal = findProposal(id);
		validateCanView(currentUser, proposal);

		return ProjectProposalResponse.fromEntity(proposal);
	}

	@Override
	@Transactional
	public ProjectProposalResponse updateProposal(String currentUserEmail, Long id, ProjectProposalUpdateRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		ProjectProposal proposal = findProposal(id);
		validateCanModify(currentUser, proposal);
		if (!EDITABLE_STATUSES.contains(proposal.getStatus())) {
			throw new BadRequestException("Proposal cannot be updated in its current status");
		}

		proposal.setTitle(request.title());
		proposal.setProblemStatement(request.problemStatement());
		proposal.setObjectives(request.objectives());
		proposal.setTargetUsers(request.targetUsers());
		proposal.setProposedFeatures(request.proposedFeatures());
		proposal.setTechnologyStack(request.technologyStack());
		proposal.setExpectedOutput(request.expectedOutput());

		return ProjectProposalResponse.fromEntity(projectProposalRepository.save(proposal));
	}

	@Override
	@Transactional
	public void deleteProposal(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		ProjectProposal proposal = findProposal(id);
		validateCanModify(currentUser, proposal);
		if (proposal.getStatus() != ProposalStatus.DRAFT && proposal.getStatus() != ProposalStatus.SUBMITTED) {
			throw new BadRequestException("Proposal cannot be deleted in its current status");
		}

		projectProposalRepository.delete(proposal);
	}

	@Override
	@Transactional
	public ProjectProposalResponse instructorDecision(String currentUserEmail, Long id, ProposalDecisionRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		if (currentUser.getRole() != Role.INSTRUCTOR && currentUser.getRole() != Role.ADMIN) {
			throw new BadRequestException("Only instructors or admins can make a proposal decision");
		}
		if (!FINAL_DECISIONS.contains(request.decision())) {
			throw new BadRequestException("Decision must be APPROVED, REVISION_REQUIRED, or REJECTED");
		}

		ProjectProposal proposal = findProposal(id);
		if (proposal.getStatus() == ProposalStatus.APPROVED || proposal.getStatus() == ProposalStatus.REJECTED) {
			throw new BadRequestException("Proposal already has a final decision");
		}
		LocalDateTime now = LocalDateTime.now();
		proposal.setInstructorRemarks(request.remarks());

		if (request.decision() == ProposalStatus.APPROVED) {
			approveProposal(proposal, now);
		} else if (request.decision() == ProposalStatus.REVISION_REQUIRED) {
			proposal.setStatus(ProposalStatus.REVISION_REQUIRED);
			proposal.setRevisionRequestedAt(now);
		} else {
			proposal.setStatus(ProposalStatus.REJECTED);
			proposal.setRejectedAt(now);
		}

		return ProjectProposalResponse.fromEntity(projectProposalRepository.save(proposal));
	}

	private void approveProposal(ProjectProposal proposal, LocalDateTime now) {
		if (projectRepository.existsByProjectProposalId(proposal.getId())) {
			throw new BadRequestException("A project already exists for this proposal");
		}

		proposal.setStatus(ProposalStatus.APPROVED);
		proposal.setApprovedAt(now);

		Project project = new Project(
				proposal.getSubmittedBy().getId(),
				proposal.getSubmittedBy().getEmail(),
				proposal.getTitle(),
				resolveProjectDescription(proposal),
				null
		);
		project.setProjectProposalId(proposal.getId());
		projectRepository.save(project);
	}

	private String resolveProjectDescription(ProjectProposal proposal) {
		if (proposal.getExpectedOutput() != null && !proposal.getExpectedOutput().isBlank()) {
			return proposal.getExpectedOutput();
		}
		return proposal.getProblemStatement();
	}

	private void applyCreateRequest(ProjectProposal proposal, ProjectProposalCreateRequest request) {
		proposal.setTitle(request.title());
		proposal.setProblemStatement(request.problemStatement());
		proposal.setObjectives(request.objectives());
		proposal.setTargetUsers(request.targetUsers());
		proposal.setProposedFeatures(request.proposedFeatures());
		proposal.setTechnologyStack(request.technologyStack());
		proposal.setExpectedOutput(request.expectedOutput());
	}

	private void validateCanView(User currentUser, ProjectProposal proposal) {
		if (currentUser.getRole() == Role.ADMIN
				|| currentUser.getRole() == Role.INSTRUCTOR
				|| currentUser.getRole() == Role.ADVISER) {
			return;
		}
		if (proposal.getSubmittedBy().getId().equals(currentUser.getId())) {
			return;
		}
		throw new ResourceNotFoundException("Project proposal not found");
	}

	private void validateCanModify(User currentUser, ProjectProposal proposal) {
		if (currentUser.getRole() == Role.ADMIN) {
			return;
		}
		if (currentUser.getRole() == Role.STUDENT && proposal.getSubmittedBy().getId().equals(currentUser.getId())) {
			return;
		}
		throw new BadRequestException("User is not allowed to modify this proposal");
	}

	private ProjectProposal findProposal(Long id) {
		return projectProposalRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project proposal not found"));
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}
}
