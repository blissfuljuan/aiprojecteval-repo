package com.blissfuljuan.aiprojecteval.projectproposal.dto;

import com.blissfuljuan.aiprojecteval.document.dto.DocumentSummaryResponse;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProjectProposal;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProposalStatus;
import java.time.LocalDateTime;

public record ProjectProposalResponse(
		Long id,
		String title,
		String problemStatement,
		String objectives,
		String targetUsers,
		String proposedFeatures,
		String technologyStack,
		String expectedOutput,
		ProposalStatus status,
		Long courseClassId,
		String courseClassName,
		Long groupId,
		String groupName,
		Long submittedByUserId,
		String submittedByName,
		String adviserRemarks,
		String instructorRemarks,
		LocalDateTime approvedAt,
		LocalDateTime rejectedAt,
		LocalDateTime revisionRequestedAt,
		DocumentSummaryResponse latestDocument,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {

	public static ProjectProposalResponse fromEntity(ProjectProposal proposal) {
		return fromEntity(proposal, null);
	}

	public static ProjectProposalResponse fromEntity(ProjectProposal proposal, DocumentSummaryResponse latestDocument) {
		String submittedByName = proposal.getSubmittedBy().getFirstName() + " " + proposal.getSubmittedBy().getLastName();

		return new ProjectProposalResponse(
				proposal.getId(),
				proposal.getTitle(),
				proposal.getProblemStatement(),
				proposal.getObjectives(),
				proposal.getTargetUsers(),
				proposal.getProposedFeatures(),
				proposal.getTechnologyStack(),
				proposal.getExpectedOutput(),
				proposal.getStatus(),
				proposal.getCourseClass().getId(),
				proposal.getCourseClass().getName(),
				null,
				null,
				proposal.getSubmittedBy().getId(),
				submittedByName,
				proposal.getAdviserRemarks(),
				proposal.getInstructorRemarks(),
				proposal.getApprovedAt(),
				proposal.getRejectedAt(),
				proposal.getRevisionRequestedAt(),
				latestDocument,
				proposal.getCreatedAt(),
				proposal.getUpdatedAt()
		);
	}

	public ProjectProposalResponse(
			Long id,
			String title,
			String problemStatement,
			String objectives,
			String targetUsers,
			String proposedFeatures,
			String technologyStack,
			String expectedOutput,
			ProposalStatus status,
			Long courseClassId,
			String courseClassName,
			Long groupId,
			String groupName,
			Long submittedByUserId,
			String submittedByName,
			String adviserRemarks,
			String instructorRemarks,
			LocalDateTime approvedAt,
			LocalDateTime rejectedAt,
			LocalDateTime revisionRequestedAt,
			LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this(
				id,
				title,
				problemStatement,
				objectives,
				targetUsers,
				proposedFeatures,
				technologyStack,
				expectedOutput,
				status,
				courseClassId,
				courseClassName,
				groupId,
				groupName,
				submittedByUserId,
				submittedByName,
				adviserRemarks,
				instructorRemarks,
				approvedAt,
				rejectedAt,
				revisionRequestedAt,
				null,
				createdAt,
				updatedAt);
	}
}
