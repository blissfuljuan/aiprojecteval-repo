export type DocumentType = "SRS" | "SDD" | "SPMP" | "STD";

export type DocumentStatus = "UPLOADED" | "PENDING_ANALYSIS" | "ANALYZED" | "NEEDS_REVIEW" | "FAILED";

export type UploadedDocument = {
  id: string;
  fileName: string;
  documentType: DocumentType;
  projectName: string;
  submissionVersion: string;
  uploadedBy: string;
  uploadedAt: string;
  fileSize: string;
  status: DocumentStatus;
};

export type AnalysisResult = {
  completenessScore: number;
  requirementCoverage: number;
  structuralAccuracy: number;
  complianceRating: string;
  findings: string[];
  suggestions: string[];
};
