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
