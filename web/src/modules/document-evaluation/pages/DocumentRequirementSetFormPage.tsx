import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { ArrowLeft } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { RequirementSetForm } from "@/modules/document-evaluation/components/ConfigurationForms";
import { documentEvaluationPaths } from "@/modules/document-evaluation/constants";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type { DocumentRequirementSet } from "@/modules/document-evaluation/types";

export function DocumentRequirementSetFormPage() {
  const navigate = useNavigate();
  const { requirementSetId } = useParams<{ requirementSetId: string }>();
  const isEdit = Boolean(requirementSetId);

  const [requirementSet, setRequirementSet] = useState<DocumentRequirementSet | null>(null);
  const [isLoading, setIsLoading] = useState(isEdit);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!requirementSetId) return;

    async function loadRequirementSet() {
      try {
        setIsLoading(true);
        setError(null);
        setRequirementSet(await documentEvaluationService.getRequirementSetById(Number(requirementSetId)));
      } catch (err) {
        setError(documentEvaluationService.getErrorMessage(err));
      } finally {
        setIsLoading(false);
      }
    }

    void loadRequirementSet();
  }, [requirementSetId]);

  async function handleSubmit(request: Parameters<typeof documentEvaluationService.createRequirementSet>[0]) {
    try {
      setIsSubmitting(true);
      setError(null);
      const saved = requirementSetId
        ? await documentEvaluationService.updateRequirementSet(Number(requirementSetId), request)
        : await documentEvaluationService.createRequirementSet(request);
      void navigate(documentEvaluationPaths.requirementSetDetails(saved.id));
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">
            {isEdit ? "Edit Requirement Set" : "Create Requirement Set"}
          </h1>
          <p className="text-sm text-muted-foreground">
            {isEdit
              ? "Update requirement set metadata before managing its document requirements."
              : "Create an assignable requirement set for classes and projects."}
          </p>
        </div>
        <Button asChild variant="outline">
          <Link to={documentEvaluationPaths.requirementSets}>
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            Back to Requirement Sets
          </Link>
        </Button>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Requirement Set Details</CardTitle>
          <CardDescription>Fields match the backend requirement set request DTO.</CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading requirement set...</p>
          ) : isEdit && !requirementSet ? (
            <p className="text-sm text-destructive">Requirement set not found.</p>
          ) : (
            <RequirementSetForm
              initialRequirementSet={requirementSet}
              isSubmitting={isSubmitting}
              onCancel={() => void navigate(documentEvaluationPaths.requirementSets)}
              onSubmit={(request) => void handleSubmit(request)}
            />
          )}
        </CardContent>
      </Card>
    </div>
  );
}
