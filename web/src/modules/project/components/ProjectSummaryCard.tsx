import type { LucideIcon } from "lucide-react";
import { Card, CardContent } from "@/common/ui/shadcn/card";

type ProjectSummaryCardProps = {
  title: string;
  value: string;
  description: string;
  icon: LucideIcon;
};

export function ProjectSummaryCard({ title, value, description, icon: Icon }: ProjectSummaryCardProps) {
  return (
    <Card>
      <CardContent className="flex items-center justify-between gap-4 p-5">
        <div className="min-w-0 space-y-1">
          <p className="text-sm font-medium text-muted-foreground">{title}</p>
          <p className="text-2xl font-semibold tracking-normal">{value}</p>
          <p className="text-xs text-muted-foreground">{description}</p>
        </div>
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-primary/10 text-primary">
          <Icon className="h-5 w-5" aria-hidden="true" />
        </div>
      </CardContent>
    </Card>
  );
}
