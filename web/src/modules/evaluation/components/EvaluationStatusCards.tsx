import { AlertCircle, CheckCircle2, Clock3, Loader2 } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const statusCards = [
  {
    title: "Pending",
    value: "1",
    description: "Awaiting AI-assisted review",
    icon: Clock3,
  },
  {
    title: "In Progress",
    value: "1",
    description: "Currently being evaluated",
    icon: Loader2,
  },
  {
    title: "Completed",
    value: "1",
    description: "Evaluation results available",
    icon: CheckCircle2,
  },
  {
    title: "Needs Review",
    value: "1",
    description: "Requires instructor attention",
    icon: AlertCircle,
  },
];

export function EvaluationStatusCards() {
  return (
    <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      {statusCards.map((card) => {
        const Icon = card.icon;

        return (
          <Card key={card.title}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">{card.title}</CardTitle>
              <Icon className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-semibold">{card.value}</div>
              <p className="mt-1 text-xs text-muted-foreground">{card.description}</p>
            </CardContent>
          </Card>
        );
      })}
    </section>
  );
}
