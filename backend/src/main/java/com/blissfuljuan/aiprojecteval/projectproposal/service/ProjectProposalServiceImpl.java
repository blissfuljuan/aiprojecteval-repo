package com.blissfuljuan.aiprojecteval.projectproposal.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassEnrollmentRepository;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentLinkSubmitRequest;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentSummaryResponse;
import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentType;
import com.blissfuljuan.aiprojecteval.document.service.DocumentService;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.AdviserDecisionRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalDocumentLinkRequest;
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
	private final CourseClassEnrollmentRepository courseClassEnrollmentRepository;
	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;
	private final DocumentService documentService;

	ProjectProposalServiceImpl(
			ProjectProposalRepository projectProposalRepository,
			CourseClassRepository courseClassRepository,
			CourseClassEnrollmentRepository courseClassEnrollmentRepository,
			UserRepository userRepository,
			ProjectRepository projectRepository,
			DocumentService documentService) {
		this.projectProposalRepository = projectProposalRepository;
		this.courseClassRepository = courseClassRepository;
		this.courseClassEnrollmentRepository = courseClassEnrollmentRepository;
		this.userRepository = userRepository;
		this.projectRepository = projectRepository;
		this.documentService = documentService;
	}

	@Override
	@Transactional
	public ProjectProposalResponse createProposal(String currentUserEmail, ProjectProposalCreateRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		CourseClass courseClass = courseClassRepository.findById(request.courseClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Course class not found"));

		if (currentUser.getRole() == Role.STUDENT
				&& !courseClassEnrollmentRepository.existsByStudentIdAndCourseClassId(
						currentUser.getId(),
						courseClass.getId())) {
			throw new BadRequestException("Students can submit proposals only for enrolled course classes");
		}

		if (projectProposalRepository.existsBySubmittedByIdAndStatusIn(currentUser.getId(), ACTIVE_STATUSES)) {
			throw new BadRequestException("User already has an active project proposal");
		}

		ProjectProposal proposal = new ProjectProposal();
		applyCreateRequest(proposal, request);
		proposal.setCourseClass(courseClass);
		proposal.setSubmittedBy(currentUser);
		proposal.setStatus(ProposalStatus.SUBMITTED);

		return toResponse(projectProposalRepository.save(proposal));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProjectProposalResponse> getMyProposals(String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);

		return projectProposalRepository.findBySubmittedById(currentUser.getId())
				.stream()
				.map(this::toResponse)
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
				.map(this::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProjectProposalResponse getProposalById(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		ProjectProposal proposal = findProposal(id);
		validateCanView(currentUser, proposal);

		return toResponse(proposal);
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

		return toResponse(projectProposalRepository.save(proposal));
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

	private static final Set<ProposalStatus> ADVISER_ACTIONABLE_STATUSES = EnumSet.of(
			ProposalStatus.SUBMITTED,
			ProposalStatus.ADVISER_REVIEW_SCHEDULED
	);
	private static final Set<ProposalStatus> ADVISER_DECISIONS = EnumSet.of(
			ProposalStatus.ADVISER_REVIEW_SCHEDULED,
			ProposalStatus.ADVISER_REVIEWED
	);

	@Override
	@Transactional
	public ProjectProposalResponse adviserDecision(String currentUserEmail, Long id, AdviserDecisionRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		if (currentUser.getRole() != Role.ADVISER && currentUser.getRole() != Role.ADMIN) {
			throw new BadRequestException("Only advisers or admins can record an adviser decision");
		}
		if (!ADVISER_DECISIONS.contains(request.decision())) {
			throw new BadRequestException("Decision must be ADVISER_REVIEW_SCHEDULED or ADVISER_REVIEWED");
		}

		ProjectProposal proposal = findProposal(id);
		if (!ADVISER_ACTIONABLE_STATUSES.contains(proposal.getStatus())) {
			throw new BadRequestException("Proposal cannot receive an adviser decision in its current status");
		}

		proposal.setStatus(request.decision());
		if (request.remarks() != null && !request.remarks().isBlank()) {
			proposal.setAdviserRemarks(request.remarks());
		}

		return toResponse(projectProposalRepository.save(proposal));
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

		return toResponse(projectProposalRepository.save(proposal));
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentResponse> getProposalDocuments(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		ProjectProposal proposal = findProposal(id);
		validateCanView(currentUser, proposal);

		return documentService.findByContextAndDocumentType(
				DocumentContextType.PROJECT_PROPOSAL,
				id,
				DocumentType.PROJECT_PROPOSAL_DOCUMENT);
	}

	@Override
	@Transactional
	public DocumentResponse submitProposalDocumentLink(
			String currentUserEmail,
			Long id,
			ProjectProposalDocumentLinkRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		ProjectProposal proposal = findProposal(id);
		validateCanModify(currentUser, proposal);

		String title = request.title();
		if (title == null || title.isBlank()) {
			title = proposal.getTitle() + " Proposal Document";
		}

		return documentService.submitExternalLink(new DocumentLinkSubmitRequest(
				DocumentContextType.PROJECT_PROPOSAL,
				id,
				DocumentType.PROJECT_PROPOSAL_DOCUMENT,
				title,
				request.description(),
				request.documentUrl()));
	}

	@Override
	@Transactional(readOnly = true)
	public void validateCanViewProposalDocuments(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		validateCanView(currentUser, findProposal(id));
	}

	@Override
	@Transactional(readOnly = true)
	public void validateCanManageProposalDocuments(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		validateCanModify(currentUser, findProposal(id));
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

	private ProjectProposalResponse toResponse(ProjectProposal proposal) {
		return ProjectProposalResponse.fromEntity(proposal, findLatestProposalDocumentSummary(proposal.getId()));
	}

	private DocumentSummaryResponse findLatestProposalDocumentSummary(Long proposalId) {
		return documentService.findByContext(DocumentContextType.PROJECT_PROPOSAL, proposalId)
				.stream()
				.filter(document -> document.documentType() == DocumentType.PROJECT_PROPOSAL_DOCUMENT)
				.findFirst()
				.orElse(null);
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
