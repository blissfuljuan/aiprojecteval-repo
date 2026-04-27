import { useParams } from "react-router";
import { Download, RotateCw } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { DocumentAnalysisSummary } from "@/modules/document/components/DocumentAnalysisSummary";
import { DocumentMetadataCard } from "@/modules/document/components/DocumentMetadataCard";
import { DocumentTabs } from "@/modules/document/components/DocumentTabs";
import { documentService } from "@/modules/document/services/documentService";

export function DocumentDetailsPage() {
  const { documentId } = useParams();
  const document = documentService.getDocumentById(documentId);
  const analysis = documentService.getAnalysisResult();

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Document Details</h1>
          <p className="text-sm text-muted-foreground">Review uploaded document metadata.</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button variant="outline">
            <Download className="h-4 w-4" aria-hidden="true" />
            Download
          </Button>
          <Button>
            <RotateCw className="h-4 w-4" aria-hidden="true" />
            Re-analyze
          </Button>
        </div>
      </div>

      <div className="grid gap-4 xl:grid-cols-[1.35fr_1fr]">
        <DocumentMetadataCard document={document} />
        <DocumentAnalysisSummary document={document} analysis={analysis} />
      </div>

      <DocumentTabs document={document} />
    </div>
  );
}
