import { FileCheck2, FileClock, FileSearch, FileWarning } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

type DocumentSummaryCardsProps = {
  totalDocuments: number;
  pendingAnalysis: number;
  analyzed: number;
  needsReview: number;
};

export function DocumentSummaryCards({
  totalDocuments,
  pendingAnalysis,
  analyzed,
  needsReview,
}: DocumentSummaryCardsProps) {
  const cards = [
    { label: "Total Documents", value: totalDocuments, icon: FileSearch },
    { label: "Pending Analysis", value: pendingAnalysis, icon: FileClock },
    { label: "Analyzed", value: analyzed, icon: FileCheck2 },
    { label: "Needs Review", value: needsReview, icon: FileWarning },
  ];

  return (
    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      {cards.map((item) => {
        const Icon = item.icon;

        return (
          <Card key={item.label}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">{item.label}</CardTitle>
              <Icon className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-semibold">{item.value}</div>
            </CardContent>
          </Card>
        );
      })}
    </div>
  );
}
