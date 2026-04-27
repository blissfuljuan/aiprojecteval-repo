import { Activity } from "lucide-react";
import { Alert, AlertDescription, AlertTitle } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { DeploymentInputCard } from "@/modules/deployment-validation/components/DeploymentInputCard";
import { ValidationLogsTable } from "@/modules/deployment-validation/components/ValidationLogsTable";
import { ValidationSummaryCards } from "@/modules/deployment-validation/components/ValidationSummaryCards";

export function DeploymentValidationPage() {
  return (
    <div className="container mx-auto flex w-full flex-col gap-6">
      <section className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Deployment Validation</h1>
          <p className="max-w-3xl text-sm text-muted-foreground">
            Validate submitted deployment URLs, expected endpoints, response status, and basic security checks.
          </p>
        </div>
        <Button type="button" size="lg" className="w-full md:w-auto">
          <Activity className="h-4 w-4" aria-hidden="true" />
          Validate Deployment
        </Button>
      </section>

      <DeploymentInputCard />

      <Alert className="border-blue-200 bg-blue-50 text-blue-900">
        <AlertTitle>Validation is currently simulated</AlertTitle>
        <AlertDescription>
          This page is a layout shell. Actual deployment checks will be connected after the backend validation API is
          implemented.
        </AlertDescription>
      </Alert>

      <ValidationSummaryCards />
      <ValidationLogsTable />
    </div>
  );
}
