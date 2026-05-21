package com.blissfuljuan.aiprojecteval.ai.provider;

import com.blissfuljuan.aiprojecteval.ai.model.AIProviderType;
import org.springframework.stereotype.Component;

@Component
public class MockAIProvider implements AIProvider {

	@Override
	public AIProviderType providerType() {
		return AIProviderType.MOCK;
	}

	@Override
	public String providerName() {
		return "mock";
	}

	@Override
	public String generate(String prompt) {
		return """
				{
				  "overallScore": 82,
				  "maxScore": 100,
				  "readinessLevel": "NEEDS_MINOR_REVISION",
				  "recommendation": "APPROVE_WITH_MINOR_IMPROVEMENTS",
				  "summary": "The proposal is coherent and mostly ready for academic review, with a clear problem framing and implementable feature direction.",
				  "strengths": [
				    "The problem statement and objectives are aligned.",
				    "The proposed features are feasible for a course project.",
				    "The technology direction is suitable for a software engineering implementation."
				  ],
				  "weaknesses": [
				    "Scope boundaries can be stated more explicitly.",
				    "Success measures and evaluation details need more precision."
				  ],
				  "missingSections": [
				    "Detailed limitations",
				    "Implementation timeline"
				  ],
				  "riskNotes": [
				    "Feature scope may expand unless core deliverables are prioritized."
				  ],
				  "suggestedRevisions": [
				    "Add a concise scope and limitations section.",
				    "Clarify target users and measurable expected outcomes.",
				    "Include implementation milestones for the first review cycle."
				  ],
				  "criteriaScores": [
				    {"criterion": "Problem Statement Clarity", "score": 9, "maxScore": 10, "rationale": "The problem is understandable and relevant."},
				    {"criterion": "Objectives Quality", "score": 8, "maxScore": 10, "rationale": "Objectives are aligned but can be more measurable."},
				    {"criterion": "Scope and Limitations", "score": 7, "maxScore": 10, "rationale": "Scope is present but limitations need sharper boundaries."},
				    {"criterion": "Target Users and Beneficiaries", "score": 8, "maxScore": 10, "rationale": "Users are identifiable with room for clearer personas."},
				    {"criterion": "Feature Feasibility", "score": 9, "maxScore": 10, "rationale": "Features appear realistic for the expected timeframe."},
				    {"criterion": "Technology Stack Appropriateness", "score": 8, "maxScore": 10, "rationale": "Stack choices are suitable for the proposed product."},
				    {"criterion": "Academic / Course Relevance", "score": 9, "maxScore": 10, "rationale": "The work fits a software project evaluation context."},
				    {"criterion": "Completeness of Required Sections", "score": 7, "maxScore": 10, "rationale": "Most required sections are present but some need more detail."},
				    {"criterion": "Originality and Practical Value", "score": 8, "maxScore": 10, "rationale": "The proposal has practical value with moderate novelty."},
				    {"criterion": "Implementation Readiness", "score": 9, "maxScore": 10, "rationale": "The project is close to being actionable."}
				  ]
				}
				""";
	}
}
