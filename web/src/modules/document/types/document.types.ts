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

export type GenericDocumentContextType = "PROJECT_PROPOSAL" | "PROJECT" | "SUBMISSION" | "EVALUATION";

export type GenericDocumentType =
  | "PROJECT_PROPOSAL_DOCUMENT"
  | "SRS"
  | "SDD"
  | "SPMP"
  | "STD"
  | "FINAL_REPORT"
  | "USER_MANUAL"
  | "DEPLOYMENT_EVIDENCE"
  | "OTHER";

export type GenericDocumentStatus = "DRAFT" | "SUBMITTED" | "ACTIVE" | "ARCHIVED";

export type DocumentSourceType = "EXTERNAL_LINK" | "FILE_UPLOAD" | "SYSTEM_GENERATED";

export type DocumentProvider = "GOOGLE_DRIVE" | "GOOGLE_DOCS" | "LOCAL_STORAGE" | "CLOUD_STORAGE" | "UNKNOWN";

export type DocumentValidationStatus =
  | "PENDING"
  | "VALID"
  | "INVALID_URL"
  | "INACCESSIBLE"
  | "UNSUPPORTED_FILE_TYPE"
  | "TOO_LARGE"
  | "NOT_A_DOCUMENT"
  | "FAILED";

export type DocumentExtractionStatus = "NOT_STARTED" | "PROCESSING" | "EXTRACTED" | "FAILED";

export type GenericDocumentVersion = {
  id: number;
  documentId: number;
  versionNumber: number;
  sourceType: DocumentSourceType;
  provider: DocumentProvider;
  originalUrl: string | null;
  externalFileId: string | null;
  storageProvider: string | null;
  storageKey: string | null;
  fileName: string | null;
  mimeType: string | null;
  fileSizeBytes: number | null;
  checksum: string | null;
  validationStatus: DocumentValidationStatus;
  extractionStatus: DocumentExtractionStatus;
  wordCount: number | null;
  submittedByUserId: number | null;
  submittedAt: string | null;
  validatedAt: string | null;
  extractedAt: string | null;
  validationMessage: string | null;
  extractionErrorMessage: string | null;
  createdAt: string;
  updatedAt: string;
};

export type GenericDocument = {
  id: number;
  contextType: GenericDocumentContextType;
  contextId: number;
  documentType: GenericDocumentType;
  title: string;
  description: string | null;
  status: GenericDocumentStatus;
  currentVersionId: number | null;
  createdByUserId: number | null;
  currentVersion: GenericDocumentVersion | null;
  createdAt: string;
  updatedAt: string;
};

export type GenericDocumentSummary = {
  id: number;
  contextType: GenericDocumentContextType;
  contextId: number;
  documentType: GenericDocumentType;
  title: string;
  status: GenericDocumentStatus;
  currentVersionId: number | null;
  currentVersionNumber: number | null;
  validationStatus: DocumentValidationStatus | null;
  extractionStatus: DocumentExtractionStatus | null;
  updatedAt: string;
};

export type DocumentLinkSubmitRequest = {
  contextType: GenericDocumentContextType;
  contextId: number;
  documentType: GenericDocumentType;
  title: string;
  description?: string;
  documentUrl: string;
};
