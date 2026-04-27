import { Badge } from "@/common/ui/shadcn/badge";
import { cn } from "@/common/lib/utils";

export type EvaluationStatus = "Pending" | "In Progress" | "Completed" | "Needs Review";

const statusStyles: Record<EvaluationStatus, string> = {
  Pending: "border-amber-200 bg-amber-50 text-amber-700 hover:bg-amber-50",
  "In Progress": "border-blue-200 bg-blue-50 text-blue-700 hover:bg-blue-50",
  Completed: "border-emerald-200 bg-emerald-50 text-emerald-700 hover:bg-emerald-50",
  "Needs Review": "border-rose-200 bg-rose-50 text-rose-700 hover:bg-rose-50",
};

type EvaluationStatusBadgeProps = {
  status: EvaluationStatus;
  className?: string;
};

export function EvaluationStatusBadge({ status, className }: EvaluationStatusBadgeProps) {
  return (
    <Badge variant="outline" className={cn(statusStyles[status], className)}>
      {status}
    </Badge>
  );
}
