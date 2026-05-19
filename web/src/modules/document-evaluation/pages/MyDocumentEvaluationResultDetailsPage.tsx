import { useParams } from "react-router";
import { DocumentEvaluationPlaceholder } from "@/modules/document-evaluation/pages/DocumentEvaluationPlaceholder";

export function MyDocumentEvaluationResultDetailsPage() {
  const { resultId } = useParams();

  return (
    <DocumentEvaluationPlaceholder
      title="Document Evaluation Result"
      description={`Published result ${resultId ?? ""} detail shell.`}
    >
      Published score, findings, evaluator feedback, and related submission file context will be shown here later.
    </DocumentEvaluationPlaceholder>
  );
}
