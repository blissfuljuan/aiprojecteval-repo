import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const evaluationStatuses = [
  { label: "Pending", value: "12", variant: "secondary" },
  { label: "In Progress", value: "8", variant: "default" },
  { label: "Needs Review", value: "4", variant: "outline" },
  { label: "Completed", value: "35", variant: "secondary" },
] as const;

export function EvaluationStatusCard() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Evaluations by Status</CardTitle>
        <CardDescription>Static evaluation workflow snapshot</CardDescription>
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
