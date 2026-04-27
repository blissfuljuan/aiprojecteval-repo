import { AlertCircle } from "lucide-react";
import { Alert, AlertDescription, AlertTitle } from "@/common/ui/shadcn/alert";
import { Badge } from "@/common/ui/shadcn/badge";

const issues = [
  {
    title: "Missing README documentation",
    severity: "Low",
    badgeClassName: "border-slate-200 bg-slate-50 text-slate-700",
  },
  {
    title: "No test folder detected",
    severity: "Medium",
    badgeClassName: "border-amber-200 bg-amber-50 text-amber-700",
  },
  {
    title: "Deployment configuration not found",
    severity: "High",
    badgeClassName: "border-red-200 bg-red-50 text-red-700",
  },
  {
    title: "Environment file should not be committed",
    severity: "High",
    badgeClassName: "border-red-200 bg-red-50 text-red-700",
  },
];

export function IssuesFoundPanel() {
  return (
    <div className="space-y-3">
      {issues.map((issue) => (
        <Alert key={issue.title}>
          <AlertCircle className="absolute left-4 top-4 h-4 w-4 text-muted-foreground" aria-hidden="true" />
          <div className="pl-6">
            <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
              <AlertTitle>{issue.title}</AlertTitle>
              <Badge variant="outline" className={issue.badgeClassName}>
                {issue.severity}
              </Badge>
            </div>
            <AlertDescription className="text-muted-foreground">
              Static placeholder issue for repository analysis review.
            </AlertDescription>
          </div>
        </Alert>
      ))}
    </div>
  );
}
