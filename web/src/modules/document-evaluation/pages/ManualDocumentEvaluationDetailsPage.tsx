import { useParams } from "react-router";
import { DocumentEvaluationPlaceholder } from "@/modules/document-evaluation/pages/DocumentEvaluationPlaceholder";

export function ManualDocumentEvaluationDetailsPage() {
  const { evaluationId } = useParams();

  return (
    <DocumentEvaluationPlaceholder
      title="Manual Evaluation Details"
      description={`Evaluation ${evaluationId ?? ""} scoring and publication shell.`}
    >
      Criterion scoring, findings, feedback, completion, return, publish, and unpublish actions will be added here.
    </DocumentEvaluationPlaceholder>
  );
}
