import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import { DocumentStatusBadge } from "@/modules/document/components/DocumentStatusBadge";
import type { GenericDocument } from "@/modules/document/types";

type DocumentMetadataCardProps = {
  document: GenericDocument;
};

export function DocumentMetadataCard({ document }: DocumentMetadataCardProps) {
  const version = document.currentVersion;
  const metadata = [
    { label: "Title", value: document.title },
    { label: "Document Type", value: formatLabel(document.documentType) },
    { label: "Context", value: `${formatLabel(document.contextType)} #${document.contextId}` },
    { label: "Current Version", value: version ? `Version ${version.versionNumber}` : "None" },
    { label: "File Name", value: version?.fileName ?? "Not available" },
    { label: "Submitted Date", value: version?.submittedAt ? new Date(version.submittedAt).toLocaleDateString() : "Not available" },
    { label: "File Size", value: formatBytes(version?.fileSizeBytes) },
  ];

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Document Metadata</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-3">
          {metadata.map((item) => (
            <div key={item.label} className="grid gap-1 sm:grid-cols-[160px_1fr]">
              <span className="text-sm text-muted-foreground">{item.label}</span>
              <span className="text-sm font-medium">{item.value}</span>
            </div>
          ))}
        </div>
        <Separator />
        <div className="flex items-center justify-between gap-3">
          <span className="text-sm text-muted-foreground">Current Status</span>
          <DocumentStatusBadge status={document.status} />
        </div>
        <div className="flex items-center justify-between gap-3">
          <span className="text-sm text-muted-foreground">Validation</span>
          <DocumentStatusBadge status={version?.validationStatus} />
        </div>
        <div className="flex items-center justify-between gap-3">
          <span className="text-sm text-muted-foreground">Extraction</span>
          <DocumentStatusBadge status={version?.extractionStatus} />
        </div>
      </CardContent>
    </Card>
  );
}

function formatLabel(value: string) {
  return value
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

function formatBytes(value: number | null | undefined) {
  if (!value) return "Not available";
  if (value < 1024) return `${value} B`;
  if (value < 1024 * 1024) return `${(value / 1024).toFixed(1)} KB`;
  return `${(value / (1024 * 1024)).toFixed(1)} MB`;
}
