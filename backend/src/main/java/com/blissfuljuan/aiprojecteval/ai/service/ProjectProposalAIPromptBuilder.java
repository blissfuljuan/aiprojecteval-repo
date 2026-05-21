package com.blissfuljuan.aiprojecteval.ai.service;

import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProjectProposal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjectProposalAIPromptBuilder {

	private static final List<String> RUBRIC = List.of(
			"Problem Statement Clarity",
			"Objectives Quality",
			"Scope and Limitations",
			"Target Users and Beneficiaries",
			"Feature Feasibility",
			"Technology Stack Appropriateness",
			"Academic / Course Relevance",
			"Completeness of Required Sections",
			"Originality and Practical Value",
			"Implementation Readiness"
	);

	public String build(ProjectProposal proposal, DocumentVersion documentVersion) {
		return """
				You are an academic software project proposal evaluator.
				Provide decision support only. Do not approve or reject the proposal.
				Evaluate using the fixed rubric below. Return strict JSON only.

				Rubric:
				%s

				ProjectProposal metadata:
				id: %d
				title: %s
				problemStatement: %s
				objectives: %s
				targetUsers: %s
				proposedFeatures: %s
				technologyStack: %s
				expectedOutput: %s
				status: %s

				Use only this extracted document text:
				%s
				""".formatted(
				String.join("\n", RUBRIC),
				proposal.getId(),
				value(proposal.getTitle()),
				value(proposal.getProblemStatement()),
				value(proposal.getObjectives()),
				value(proposal.getTargetUsers()),
				value(proposal.getProposedFeatures()),
				value(proposal.getTechnologyStack()),
				value(proposal.getExpectedOutput()),
				proposal.getStatus(),
				value(documentVersion.getExtractedText()));
	}

	private String value(String value) {
		if (value == null || value.isBlank()) {
			return "N/A";
		}
		return value;
	}
}
