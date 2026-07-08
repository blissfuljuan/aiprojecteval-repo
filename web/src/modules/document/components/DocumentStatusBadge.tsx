import { Badge } from "@/common/ui/shadcn/badge";
import type { DocumentExtractionStatus, DocumentStatus, DocumentValidationStatus, GenericDocumentStatus } from "@/modules/document/types";

type AnyDocumentStatus = DocumentStatus | GenericDocumentStatus | DocumentValidationStatus | DocumentExtractionStatus;

const statusConfig: Record<string, { label: string; className: string }> = {
  UPLOADED: { label: "Uploaded", className: "border-slate-200 bg-slate-50 text-slate-700" },
  PENDING_ANALYSIS: { label: "Pending Analysis", className: "border-amber-200 bg-amber-50 text-amber-700" },
  ANALYZED: { label: "Analyzed", className: "border-emerald-200 bg-emerald-50 text-emerald-700" },
  NEEDS_REVIEW: { label: "Needs Review", className: "border-rose-200 bg-rose-50 text-rose-700" },
  FAILED: { label: "Failed", className: "border-red-200 bg-red-50 text-red-700" },
  DRAFT: { label: "Draft", className: "border-slate-200 bg-slate-50 text-slate-700" },
  SUBMITTED: { label: "Submitted", className: "border-blue-200 bg-blue-50 text-blue-700" },
  ACTIVE: { label: "Active", className: "border-emerald-200 bg-emerald-50 text-emerald-700" },
  ARCHIVED: { label: "Archived", className: "border-slate-200 bg-slate-50 text-slate-700" },
  PENDING: { label: "Pending", className: "border-amber-200 bg-amber-50 text-amber-700" },
  VALID: { label: "Valid", className: "border-emerald-200 bg-emerald-50 text-emerald-700" },
  INVALID_URL: { label: "Invalid URL", className: "border-rose-200 bg-rose-50 text-rose-700" },
  INACCESSIBLE: { label: "Inaccessible", className: "border-rose-200 bg-rose-50 text-rose-700" },
  UNSUPPORTED_FILE_TYPE: { label: "Unsupported Type", className: "border-rose-200 bg-rose-50 text-rose-700" },
  TOO_LARGE: { label: "Too Large", className: "border-rose-200 bg-rose-50 text-rose-700" },
  NOT_A_DOCUMENT: { label: "Not A Document", className: "border-rose-200 bg-rose-50 text-rose-700" },
  NOT_STARTED: { label: "Not Started", className: "border-slate-200 bg-slate-50 text-slate-700" },
  PROCESSING: { label: "Processing", className: "border-blue-200 bg-blue-50 text-blue-700" },
  EXTRACTED: { label: "Extracted", className: "border-emerald-200 bg-emerald-50 text-emerald-700" },
  COMPLETED: { label: "Completed", className: "border-emerald-200 bg-emerald-50 text-emerald-700" },
};

type DocumentStatusBadgeProps = {
  status: AnyDocumentStatus | string | null | undefined;
};

export function DocumentStatusBadge({ status }: DocumentStatusBadgeProps) {
  const config = status ? statusConfig[status] : null;

  return (
    <Badge variant="outline" className={config?.className ?? "border-slate-200 bg-slate-50 text-slate-700"}>
      {config?.label ?? "Not available"}
    </Badge>
  );
}
