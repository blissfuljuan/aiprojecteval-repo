import { Badge } from "@/common/ui/shadcn/badge";
import type { ProposalStatus } from "@/modules/project-proposal/types";

const statusConfig: Record<ProposalStatus, { label: string; variant: "default" | "secondary" | "outline" | "destructive" }> = {
  DRAFT: { label: "Draft", variant: "secondary" },
  SUBMITTED: { label: "Submitted", variant: "default" },
  ADVISER_REVIEW_SCHEDULED: { label: "Adviser Review Scheduled", variant: "outline" },
  ADVISER_REVIEWED: { label: "Adviser Reviewed", variant: "outline" },
  INSTRUCTOR_REVIEW_SCHEDULED: { label: "Instructor Review Scheduled", variant: "outline" },
  REVISION_REQUIRED: { label: "Revision Required", variant: "destructive" },
  APPROVED: { label: "Approved", variant: "default" },
  REJECTED: { label: "Rejected", variant: "destructive" },
};

type Props = { status: ProposalStatus };

export function ProposalStatusBadge({ status }: Props) {
  const config = statusConfig[status];
  return <Badge variant={config.variant}>{config.label}</Badge>;
}
