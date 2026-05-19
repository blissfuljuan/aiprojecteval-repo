import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { ArrowLeft } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { RequirementPresetForm } from "@/modules/document-evaluation/components/ConfigurationForms";
import { documentEvaluationPaths } from "@/modules/document-evaluation/constants";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type { DocumentRequirementPreset } from "@/modules/document-evaluation/types";

export function DocumentRequirementPresetFormPage() {
  const navigate = useNavigate();
  const { presetId } = useParams<{ presetId: string }>();
  const isEdit = Boolean(presetId);

  const [preset, setPreset] = useState<DocumentRequirementPreset | null>(null);
  const [isLoading, setIsLoading] = useState(isEdit);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!presetId) return;

    async function loadPreset() {
      try {
        setIsLoading(true);
        setError(null);
        setPreset(await documentEvaluationService.getPresetById(Number(presetId)));
      } catch (err) {
        setError(documentEvaluationService.getErrorMessage(err));
      } finally {
        setIsLoading(false);
      }
    }

    void loadPreset();
  }, [presetId]);

  async function handleSubmit(request: Parameters<typeof documentEvaluationService.createPreset>[0]) {
    try {
      setIsSubmitting(true);
      setError(null);
      if (presetId) {
        await documentEvaluationService.updatePreset(Number(presetId), request);
      } else {
        await documentEvaluationService.createPreset(request);
      }
      void navigate(documentEvaluationPaths.presets);
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
          <h1 className="text-3xl font-semibold tracking-normal">{isEdit ? "Edit Preset" : "Create Preset"}</h1>
          <p className="text-sm text-muted-foreground">
            {isEdit
              ? "Update preset metadata, visibility, and configuration status."
              : "Create a reusable document requirement preset for later requirement set configuration."}
          </p>
        </div>
        <Button asChild variant="outline">
          <Link to={documentEvaluationPaths.presets}>
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            Back to Presets
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
          <CardTitle className="text-lg">Preset Details</CardTitle>
          <CardDescription>Fields match the backend preset request DTO.</CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading preset...</p>
          ) : isEdit && !preset ? (
            <p className="text-sm text-destructive">Preset not found.</p>
          ) : (
            <RequirementPresetForm
              initialPreset={preset}
              isSubmitting={isSubmitting}
              onCancel={() => void navigate(documentEvaluationPaths.presets)}
              onSubmit={(request) => void handleSubmit(request)}
            />
          )}
        </CardContent>
      </Card>
    </div>
  );
}
