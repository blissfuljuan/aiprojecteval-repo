export type AiModule = "ai";

export type AIProviderType = "MOCK" | "OPENAI" | "OLLAMA";

export type AIEvaluationStatus = "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";

export type ProposalReadinessLevel = "READY" | "MOSTLY_READY" | "NEEDS_REVISION" | "HIGH_RISK";

export type ProposalAIRecommendation = "APPROVE" | "APPROVE_WITH_MINOR_REVISIONS" | "REQUEST_REVISION" | "REJECT";

export type ProjectProposalAIEvaluationRequest = {
  documentVersionId: number;
  provider?: AIProviderType;
  forceReevaluate?: boolean;
};

export type AICriteriaScore = {
  criterion: string;
  score: number;
  maxScore: number;
  rationale: string | null;
};

export type ProjectProposalAIEvaluation = {
  id: number;
  evaluationId: number;
  proposalId: number;
  documentVersionId: number;
  status: AIEvaluationStatus;
  provider: AIProviderType;
  model: string | null;
  overallScore: number | null;
  maxScore: number | null;
  readinessLevel: ProposalReadinessLevel | null;
  recommendation: ProposalAIRecommendation | null;
  summary: string | null;
  strengths: string[];
  weaknesses: string[];
  missingSections: string[];
  riskNotes: string[];
  suggestedRevisions: string[];
  criteriaScores: AICriteriaScore[];
  createdAt: string;
  completedAt: string | null;
};
