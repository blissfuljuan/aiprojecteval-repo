import { Bot, CheckCircle2, FileCheck2, Github } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const summaryItems = [
  { label: "Documents Uploaded", value: "4/4", status: "Complete", icon: FileCheck2 },
  { label: "Repository Status", value: "Connected", status: "Ready", icon: Github },
  { label: "Deployment Status", value: "Reachable", status: "Online", icon: CheckCircle2 },
  { label: "Evaluation Status", value: "Pending Review", status: "Queued", icon: Bot },
];

export function SubmissionSummaryCards() {
  return (
    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      {summaryItems.map((item) => {
        const Icon = item.icon;
        return (
          <Card key={item.label}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">{item.label}</CardTitle>
              <Icon className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="text-2xl font-semibold">{item.value}</div>
              <Badge variant="secondary">{item.status}</Badge>
            </CardContent>
          </Card>
        );
      })}
    </div>
  );
}
