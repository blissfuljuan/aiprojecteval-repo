import { Badge } from "@/common/ui/shadcn/badge";
import { cn } from "@/common/lib/utils";
import type { SubmissionStatus } from "@/modules/submission/types";

const statusClasses: Record<SubmissionStatus, string> = {
  Draft: "border-slate-200 bg-slate-50 text-slate-700",
  Submitted: "border-blue-200 bg-blue-50 text-blue-700",
  "Under Review": "border-amber-200 bg-amber-50 text-amber-700",
  "Needs Revision": "border-red-200 bg-red-50 text-red-700",
  Evaluated: "border-emerald-200 bg-emerald-50 text-emerald-700",
};

interface SubmissionStatusBadgeProps {
  status: SubmissionStatus;
  className?: string;
}

export function SubmissionStatusBadge({ status, className }: SubmissionStatusBadgeProps) {
  return (
    <Badge variant="outline" className={cn(statusClasses[status], className)}>
      {status}
    </Badge>
  );
}
