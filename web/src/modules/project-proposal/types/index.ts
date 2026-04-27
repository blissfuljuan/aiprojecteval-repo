export type ProposalStatus =
  | "DRAFT"
  | "SUBMITTED"
  | "ADVISER_REVIEW_SCHEDULED"
  | "ADVISER_REVIEWED"
  | "INSTRUCTOR_REVIEW_SCHEDULED"
  | "REVISION_REQUIRED"
  | "APPROVED"
  | "REJECTED";

export type ProjectProposal = {
  id: number;
  title: string;
  problemStatement: string;
  objectives: string;
  targetUsers: string | null;
  proposedFeatures: string;
  technologyStack: string | null;
  expectedOutput: string | null;
  status: ProposalStatus;
  courseClassId: number;
  courseClassName: string;
  groupId: number | null;
  groupName: string | null;
  submittedByUserId: number;
  submittedByName: string;
  adviserRemarks: string | null;
  instructorRemarks: string | null;
  approvedAt: string | null;
  rejectedAt: string | null;
  revisionRequestedAt: string | null;
  createdAt: string;
  updatedAt: string;
};

export type CourseClass = {
  id: number;
  name: string;
  code: string | null;
  createdAt: string;
  updatedAt: string;
};

export type ProposalCreateRequest = {
  title: string;
  problemStatement: string;
  objectives: string;
  targetUsers?: string;
  proposedFeatures: string;
  technologyStack?: string;
  expectedOutput?: string;
  courseClassId: number;
};

export type ProposalUpdateRequest = {
  title: string;
  problemStatement: string;
  objectives: string;
  targetUsers?: string;
  proposedFeatures: string;
  technologyStack?: string;
  expectedOutput?: string;
};

export type AdviserDecisionRequest = {
  decision: "ADVISER_REVIEW_SCHEDULED" | "ADVISER_REVIEWED";
  remarks?: string;
};

export type InstructorDecisionRequest = {
  decision: "APPROVED" | "REVISION_REQUIRED" | "REJECTED";
  remarks: string;
};
