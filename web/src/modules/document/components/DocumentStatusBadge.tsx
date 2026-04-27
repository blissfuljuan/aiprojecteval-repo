import { Badge } from "@/common/ui/shadcn/badge";
import type { DocumentStatus } from "@/modules/document/types";

const statusConfig: Record<DocumentStatus, { label: string; className: string }> = {
  UPLOADED: { label: "Uploaded", className: "border-slate-200 bg-slate-50 text-slate-700" },
  PENDING_ANALYSIS: { label: "Pending Analysis", className: "border-amber-200 bg-amber-50 text-amber-700" },
  ANALYZED: { label: "Analyzed", className: "border-emerald-200 bg-emerald-50 text-emerald-700" },
  NEEDS_REVIEW: { label: "Needs Review", className: "border-rose-200 bg-rose-50 text-rose-700" },
  FAILED: { label: "Failed", className: "border-red-200 bg-red-50 text-red-700" },
};

type DocumentStatusBadgeProps = {
  status: DocumentStatus;
};

export function DocumentStatusBadge({ status }: DocumentStatusBadgeProps) {
  const config = statusConfig[status];

  return (
    <Badge variant="outline" className={config.className}>
      {config.label}
    </Badge>
  );
}
