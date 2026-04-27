import { CheckCircle2, FolderTree, GitCommitHorizontal, ShieldCheck } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const repositorySummary = [
  {
    title: "Repository Found",
    value: "Connected",
    badge: "Success",
    icon: CheckCircle2,
    badgeClassName: "border-emerald-200 bg-emerald-50 text-emerald-700",
  },
  {
    title: "Commit Activity",
    value: "48 commits",
    badge: "Active",
    icon: GitCommitHorizontal,
    badgeClassName: "border-blue-200 bg-blue-50 text-blue-700",
  },
  {
    title: "Folder Structure",
    value: "Valid",
    badge: "Passed",
    icon: FolderTree,
    badgeClassName: "border-emerald-200 bg-emerald-50 text-emerald-700",
  },
  {
    title: "Code Evidence",
    value: "Detected",
    badge: "Review",
    icon: ShieldCheck,
    badgeClassName: "border-amber-200 bg-amber-50 text-amber-700",
  },
];

export function RepositorySummaryCards() {
  return (
    <section className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      {repositorySummary.map((item) => {
        const Icon = item.icon;

        return (
          <Card key={item.title}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">{item.title}</CardTitle>
              <Icon className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="text-2xl font-semibold">{item.value}</div>
              <Badge variant="outline" className={item.badgeClassName}>
                {item.badge}
              </Badge>
            </CardContent>
          </Card>
        );
      })}
    </section>
  );
}
