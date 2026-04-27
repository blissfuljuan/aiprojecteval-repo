import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import { documentService } from "@/modules/document/services/documentService";

export function DocumentVersionHistory() {
  const history = documentService.getDocumentHistory();

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Document History</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {history.map((item, index) => (
          <div key={`${item.version}-${item.event}`}>
            <div className="grid gap-1 sm:grid-cols-[96px_1fr_auto] sm:items-center">
              <span className="text-sm font-medium">{item.version}</span>
              <span className="text-sm">{item.event}</span>
              <span className="text-sm text-muted-foreground">{item.date}</span>
            </div>
            <p className="mt-1 text-xs text-muted-foreground">By {item.actor}</p>
            {index < history.length - 1 ? <Separator className="mt-4" /> : null}
          </div>
        ))}
      </CardContent>
    </Card>
  );
}
