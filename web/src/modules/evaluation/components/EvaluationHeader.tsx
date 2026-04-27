import { Card, CardContent } from "@/common/ui/shadcn/card";
import { Progress } from "@/common/ui/shadcn/progress";
import { EvaluationStatusBadge } from "@/modules/evaluation/components/EvaluationStatusBadge";

export function EvaluationHeader() {
  return (
    <Card>
      <CardContent className="flex flex-col gap-5 p-6 lg:flex-row lg:items-center lg:justify-between">
        <div className="space-y-2">
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-3xl font-semibold tracking-normal">Campus Clinic System</h1>
            <EvaluationStatusBadge status="Completed" />
          </div>
          <p className="text-sm text-muted-foreground">Final Submission evaluated on Apr 25, 2026</p>
        </div>
        <div className="w-full max-w-sm space-y-2">
          <div className="flex items-end justify-between gap-4">
            <span className="text-sm font-medium text-muted-foreground">Compliance Score</span>
            <span className="text-3xl font-semibold">86%</span>
          </div>
          <Progress value={86} />
        </div>
      </CardContent>
    </Card>
  );
}
