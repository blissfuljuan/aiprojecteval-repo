import { PlayCircle } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { EvaluationStatusCards } from "@/modules/evaluation/components/EvaluationStatusCards";
import { EvaluationTable } from "@/modules/evaluation/components/EvaluationTable";

export function EvaluationDashboardPage() {
  return (
    <div className="flex w-full flex-col gap-6">
      <section className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Evaluations</h1>
          <p className="text-sm text-muted-foreground">
            Review AI-assisted compliance analysis and project evaluations.
          </p>
        </div>
        <Button type="button">
          <PlayCircle className="h-4 w-4" aria-hidden="true" />
          Start Evaluation
        </Button>
      </section>

      <EvaluationStatusCards />
      <EvaluationTable />
    </div>
  );
}
