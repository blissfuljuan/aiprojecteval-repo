import { SearchCode } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { RepositoryAnalysisTabs } from "@/modules/repository-analysis/components/RepositoryAnalysisTabs";
import { RepositoryInputCard } from "@/modules/repository-analysis/components/RepositoryInputCard";
import { RepositorySummaryCards } from "@/modules/repository-analysis/components/RepositorySummaryCards";

export function RepositoryAnalysisPage() {
  return (
    <div className="container mx-auto flex w-full flex-col gap-6">
      <section className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Repository Analysis</h1>
          <p className="text-sm text-muted-foreground">
            Inspect repository structure, commits, modules, and code evidence.
          </p>
        </div>
        <Button type="button" size="lg" className="w-full md:w-auto">
          <SearchCode className="h-4 w-4" aria-hidden="true" />
          Analyze Repository
        </Button>
      </section>

      <RepositoryInputCard />
      <RepositorySummaryCards />
      <RepositoryAnalysisTabs />
    </div>
  );
}
