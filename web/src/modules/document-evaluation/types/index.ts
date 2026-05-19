export type QueryParams = Record<string, string | number | boolean | null | undefined>;

export type RequestBody = Record<string, unknown>;

export type FileDownloadResponse = Blob;

export type ConfigurationStatus = "DRAFT" | "ACTIVE" | "ARCHIVED";

export type PresetVisibility = "SYSTEM" | "INSTRUCTOR_PRIVATE" | "DEPARTMENT_SHARED";

export type AllowedFileType =
  | "PDF"
  | "DOC"
  | "DOCX"
  | "XLS"
  | "XLSX"
  | "PPT"
  | "PPTX"
  | "PNG"
  | "JPG"
  | "JPEG"
  | "TXT"
  | "ZIP";

export type RequirementSetAssignmentType = "COURSE_CLASS" | "PROJECT";

export type RequirementSetAssignmentStatus = "ACTIVE" | "INACTIVE" | "ARCHIVED";

export type DocumentRequirementPresetRequest = {
  name: string;
  description?: string;
  category?: string;
  visibility: PresetVisibility;
  status?: ConfigurationStatus;
};

export type CopyPresetRequest = {
  name: string;
  description?: string;
};

export type PresetDocumentRequirementRequest = {
  name: string;
  description?: string;
  required?: boolean;
  allowedFileTypes?: AllowedFileType[];
  sortOrder: number;
  templateId?: number;
  rubricId?: number;
};

export type RequirementSetRequest = {
  name: string;
  description?: string;
};

export type DocumentRequirementRequest = {
  name: string;
  description?: string;
  required?: boolean;
  allowedFileTypes: AllowedFileType[];
  sortOrder?: number;
  templateId?: number;
  rubricId?: number;
};

export type UpdateDocumentRequirementRequest = DocumentRequirementRequest & {
  sortOrder: number;
};

export type ReorderDocumentRequirementsRequest = {
  items: Array<{
    requirementId: number;
    sortOrder: number;
  }>;
};

export type AssignRequirementSetToClassRequest = {
  requirementSetId: number;
  courseClassId: number;
  notes?: string;
};

export type AssignRequirementSetToProjectRequest = {
  requirementSetId: number;
  projectId: number;
  notes?: string;
};

export type DeactivateRequirementSetAssignmentRequest = {
  notes?: string;
};

export type PresetDocumentRequirement = {
  id: number;
  presetId: number;
  name: string;
  description: string | null;
  required: boolean;
  allowedFileTypes: AllowedFileType[] | null;
  sortOrder: number;
  templateId: number | null;
  templateName: string | null;
  rubricId: number | null;
  rubricName: string | null;
  createdAt: string;
  updatedAt: string;
};

export type DocumentRequirementPreset = {
  id: number;
  name: string;
  description: string | null;
  category: string | null;
  visibility: PresetVisibility;
  status: ConfigurationStatus;
  createdById: number | null;
  createdByName: string | null;
  createdByEmail: string | null;
  createdAt: string;
  updatedAt: string;
  documentRequirements: PresetDocumentRequirement[] | null;
};

export type DocumentTemplateSummary = {
  id?: number;
  name?: string;
  title?: string;
};

export type EvaluationRubricSummary = {
  id?: number;
  name?: string;
  title?: string;
};

export type DocumentRequirement = {
  id: number;
  requirementSetId: number;
  name: string;
  description: string | null;
  required: boolean;
  allowedFileTypes: AllowedFileType[] | null;
  sortOrder: number;
  template: DocumentTemplateSummary | null;
  rubric: EvaluationRubricSummary | null;
  createdAt: string;
  updatedAt: string;
};

export type DocumentRequirementSetSummary = {
  id: number;
  name: string;
  description: string | null;
  sourcePresetId: number | null;
  ownerInstructorId: number | null;
  ownerInstructorName: string | null;
  ownerInstructorEmail: string | null;
  status: ConfigurationStatus;
  createdAt: string;
  updatedAt: string;
  documentRequirementCount: number;
};

export type DocumentRequirementSet = DocumentRequirementSetSummary & {
  documentRequirements: DocumentRequirement[] | null;
};

export type DocumentRequirementSetAssignmentSummary = {
  id: number;
  requirementSetId: number;
  requirementSetName: string;
  assignmentType: RequirementSetAssignmentType;
  status: RequirementSetAssignmentStatus;
  courseClassId: number | null;
  courseClassName: string | null;
  projectId: number | null;
  projectTitle: string | null;
  assignedById: number | null;
  assignedByEmail: string | null;
  assignedAt: string | null;
  deactivatedAt: string | null;
  notes: string | null;
};

export type DocumentRequirementSetAssignment = Partial<DocumentRequirementSetAssignmentSummary> & {
  id: number;
  requirementSet?: DocumentRequirementSetSummary;
  courseClass?: {
    id?: number;
    name?: string;
    code?: string | null;
  } | null;
  project?: {
    id?: number;
    title?: string;
    name?: string;
  } | null;
  assignedBy?: {
    id?: number;
    email?: string;
    name?: string;
  } | null;
};
