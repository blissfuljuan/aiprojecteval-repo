import type { Role } from "@/modules/identity/types";

export const documentEvaluationPaths = {
  home: "/document-evaluation",
  presets: "/document-evaluation/presets",
  presetCreate: "/document-evaluation/presets/new",
  presetDetails: (presetId: string | number) => `/document-evaluation/presets/${presetId}`,
  presetEdit: (presetId: string | number) => `/document-evaluation/presets/${presetId}/edit`,
  requirementSets: "/document-evaluation/requirement-sets",
  requirementSetCreate: "/document-evaluation/requirement-sets/new",
  requirementSetDetails: (requirementSetId: string | number) => `/document-evaluation/requirement-sets/${requirementSetId}`,
  requirementSetEdit: (requirementSetId: string | number) => `/document-evaluation/requirement-sets/${requirementSetId}/edit`,
  assignments: "/document-evaluation/assignments",
  submissions: "/document-evaluation/submissions",
  completeness: "/document-evaluation/completeness",
  evaluations: "/document-evaluation/evaluations",
  evaluationDetails: (evaluationId: string | number) => `/document-evaluation/evaluations/${evaluationId}`,
  myResults: "/document-evaluation/my-results",
  myResultDetails: (resultId: string | number) => `/document-evaluation/my-results/${resultId}`,
};

export const documentEvaluationAdminRoles: Role[] = ["ADMIN"];
export const documentEvaluationInstructorRoles: Role[] = ["ADMIN", "INSTRUCTOR"];
export const documentEvaluationEvaluatorRoles: Role[] = ["ADMIN", "INSTRUCTOR", "EVALUATOR"];
export const documentEvaluationOverviewRoles: Role[] = ["ADMIN", "INSTRUCTOR", "EVALUATOR", "STUDENT"];
export const documentEvaluationSubmissionRoles: Role[] = ["ADMIN", "INSTRUCTOR", "STUDENT"];
export const documentEvaluationStudentOnlyRoles: Role[] = ["STUDENT"];

export const documentEvaluationEndpointGroups = {
  presets: "/api/document-evaluation/presets",
  requirementSets: "/api/document-evaluation/requirement-sets",
  assignments: "/api/document-evaluation/requirement-set-assignments",
  completeness: "/api/document-evaluation",
  evaluations: "/api/document-evaluation/evaluations",
  results: "/api/document-evaluation/evaluation-results",
};
