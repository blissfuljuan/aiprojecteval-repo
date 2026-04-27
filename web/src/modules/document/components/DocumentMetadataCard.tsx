import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import { DocumentStatusBadge } from "@/modules/document/components/DocumentStatusBadge";
import type { UploadedDocument } from "@/modules/document/types";

type DocumentMetadataCardProps = {
  document: UploadedDocument;
};

export function DocumentMetadataCard({ document }: DocumentMetadataCardProps) {
  const metadata = [
    { label: "File Name", value: document.fileName },
    { label: "Document Type", value: document.documentType },
    { label: "Project", value: document.projectName },
    { label: "Submission Version", value: document.submissionVersion },
    { label: "Uploaded By", value: document.uploadedBy },
    { label: "Uploaded Date", value: document.uploadedAt },
    { label: "File Size", value: document.fileSize },
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
      </CardContent>
    </Card>
  );
}
