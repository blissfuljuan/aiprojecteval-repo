import { CheckCircle2, Circle, Dot } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent } from "@/common/ui/shadcn/card";
import { cn } from "@/common/lib/utils";

const steps = [
  { number: "01", title: "Basic Information", state: "active" },
  { number: "02", title: "Required Documents", state: "pending" },
  { number: "03", title: "Repository & Deployment", state: "pending" },
  { number: "04", title: "Review Submission", state: "pending" },
] as const;

export function SubmissionStepper() {
  return (
    <Card>
      <CardContent className="grid gap-3 p-4 md:grid-cols-4">
        {steps.map((step) => {
          const isActive = step.state === "active";
          return (
            <div
              key={step.number}
              className={cn(
                "flex min-h-20 items-start gap-3 rounded-md border p-3",
                isActive ? "border-primary bg-primary/5" : "border-border bg-background",
              )}
            >
              <div
                className={cn(
                  "flex h-9 w-9 shrink-0 items-center justify-center rounded-full border text-xs font-semibold",
                  isActive ? "border-primary bg-primary text-primary-foreground" : "border-muted-foreground/30 bg-muted",
                )}
              >
                {isActive ? <Dot className="h-5 w-5" aria-hidden="true" /> : <Circle className="h-4 w-4" aria-hidden="true" />}
              </div>
              <div className="min-w-0 space-y-2">
                <p className="text-xs font-medium text-muted-foreground">{step.number}</p>
                <p className="text-sm font-semibold leading-snug">{step.title}</p>
                <Badge variant={isActive ? "default" : "secondary"} className="w-fit">
                  {isActive ? (
                    <>
                      <CheckCircle2 className="h-3 w-3" aria-hidden="true" />
                      Active
                    </>
                  ) : (
                    "Pending"
                  )}
                </Badge>
              </div>
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
}
