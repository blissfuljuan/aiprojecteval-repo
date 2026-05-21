export type SubmissionStatus = "Draft" | "Submitted" | "Under Review" | "Needs Revision" | "Evaluated";

export type DocumentType = "SRS" | "SDD" | "SPMP" | "STD";

export interface SubmissionDocument {
  id: string;
  type: DocumentType;
  fileName: string;
  status: "Required" | "Pending" | "Uploaded";
  uploadedAt: string;
}

export interface SubmissionSummary {
  id: string;
  projectName: string;
  title: string;
  status: SubmissionStatus;
  documentsUploaded: string;
  repositoryStatus: string;
  deploymentStatus: string;
  evaluationStatus: string;
  githubUrl: string;
  deploymentUrl: string;
}

export type DocumentSubmissionStatus = "DRAFT" | "SUBMITTED" | "RETURNED" | "RESUBMITTED" | "ACCEPTED" | "ARCHIVED";

export type SubmissionType =
  | "DOCUMENT"
  | "PROJECT_PROPOSAL"
  | "REPOSITORY_LINK"
  | "DEPLOYMENT_LINK"
  | "PRESENTATION"
  | "OTHER";

export type SubmissionFileStatus = "PENDING_UPLOAD" | "UPLOADED" | "REPLACED" | "REMOVED" | "FAILED";

export type SubmissionFileResponse = {
  id: number;
  submissionId: number;
  originalFileName: string | null;
  storedFileName: string | null;
  fileUrl: string | null;
  downloadUrl: string | null;
  viewUrl: string | null;
  contentType: string | null;
  fileSize: number | null;
  fileExtension: string | null;
  checksum: string | null;
  fileStatus: SubmissionFileStatus;
  uploadedAt: string | null;
};

export type DocumentSubmissionSummary = {
  id: number;
  type: SubmissionType;
  assignmentId: number;
  requirementId: number;
  projectId: number | null;
  courseClassId: number | null;
  submittedById: number | null;
  submittedByName: string | null;
  status: DocumentSubmissionStatus;
  submissionTitle: string;
  attemptNumber: number | null;
  submittedAt: string | null;
  lastUpdatedAt: string | null;
};

export type DocumentSubmission = DocumentSubmissionSummary & {
  submissionNotes: string | null;
  files: SubmissionFileResponse[] | null;
};

export type CreateSubmissionDraftRequest = {
  assignmentId: number;
  requirementId: number;
  submissionTitle: string;
  submissionNotes?: string;
};

export type UpdateSubmissionDraftRequest = {
  submissionTitle: string;
  submissionNotes?: string;
};
