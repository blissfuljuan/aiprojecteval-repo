import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const projectStatuses = [
  { label: "Active", value: "10", variant: "default" },
  { label: "Under Review", value: "6", variant: "secondary" },
  { label: "Completed", value: "5", variant: "outline" },
  { label: "Archived", value: "3", variant: "secondary" },
] as const;

export function ProjectStatusCard() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Projects by Status</CardTitle>
        <CardDescription>Current project lifecycle distribution</CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        {projectStatuses.map((status) => (
          <div key={status.label} className="flex items-center justify-between gap-4">
            <span className="text-sm text-muted-foreground">{status.label}</span>
            <Badge variant={status.variant}>{status.value}</Badge>
          </div>
        ))}
      </CardContent>
    </Card>
  );
}
