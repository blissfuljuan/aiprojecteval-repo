import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const evaluationStatuses = [
  { label: "Pending", value: "8", variant: "secondary" },
  { label: "In Progress", value: "5", variant: "default" },
  { label: "Completed", value: "21", variant: "outline" },
  { label: "Needs Revision", value: "4", variant: "secondary" },
] as const;

export function EvaluationStatusCard() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Evaluation Status</CardTitle>
        <CardDescription>Static evaluation workflow summary</CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        {evaluationStatuses.map((status) => (
          <div key={status.label} className="flex items-center justify-between gap-4">
            <span className="text-sm text-muted-foreground">{status.label}</span>
            <Badge variant={status.variant}>{status.value}</Badge>
          </div>
        ))}
      </CardContent>
    </Card>
  );
}
