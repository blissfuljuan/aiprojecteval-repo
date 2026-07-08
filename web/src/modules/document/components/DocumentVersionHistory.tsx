import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import { DocumentStatusBadge } from "@/modules/document/components/DocumentStatusBadge";
import type { GenericDocumentVersion } from "@/modules/document/types";

type DocumentVersionHistoryProps = {
  versions: GenericDocumentVersion[];
};

export function DocumentVersionHistory({ versions }: DocumentVersionHistoryProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Document History</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {versions.length === 0 && <p className="text-sm text-muted-foreground">No document versions found.</p>}
        {versions.map((version, index) => (
          <div key={version.id}>
            <div className="grid gap-2 sm:grid-cols-[96px_1fr_auto] sm:items-center">
              <span className="text-sm font-medium">Version {version.versionNumber}</span>
              <span className="text-sm">{version.fileName ?? version.originalUrl ?? "External document link"}</span>
              <span className="text-sm text-muted-foreground">{new Date(version.createdAt).toLocaleDateString()}</span>
            </div>
            <div className="mt-2 flex flex-wrap gap-2">
              <DocumentStatusBadge status={version.validationStatus} />
              <DocumentStatusBadge status={version.extractionStatus} />
            </div>
            {version.validationMessage && <p className="mt-2 text-xs text-muted-foreground">{version.validationMessage}</p>}
            {version.extractionErrorMessage && (
              <p className="mt-2 text-xs text-destructive">{version.extractionErrorMessage}</p>
            )}
            {index < versions.length - 1 ? <Separator className="mt-4" /> : null}
          </div>
        ))}
      </CardContent>
    </Card>
  );
}
