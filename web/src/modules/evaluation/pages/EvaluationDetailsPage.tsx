import { EvaluationActionArea } from "@/modules/evaluation/components/EvaluationActionArea";
import { EvaluationFindingsTabs } from "@/modules/evaluation/components/EvaluationFindingsTabs";
import { EvaluationHeader } from "@/modules/evaluation/components/EvaluationHeader";
import { EvaluationScoreSummary } from "@/modules/evaluation/components/EvaluationScoreSummary";

export function EvaluationDetailsPage() {
  return (
    <div className="flex w-full flex-col gap-6">
      <EvaluationHeader />
      <EvaluationScoreSummary />
      <EvaluationFindingsTabs />
      <EvaluationActionArea />
    </div>
  );
}
