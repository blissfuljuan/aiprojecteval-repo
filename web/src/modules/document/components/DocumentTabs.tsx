import { Card, CardContent } from "@/common/ui/shadcn/card";
import { ScrollArea } from "@/common/ui/shadcn/scroll-area";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/common/ui/shadcn/tabs";
import { DocumentVersionHistory } from "@/modules/document/components/DocumentVersionHistory";
import type { GenericDocument, GenericDocumentVersion } from "@/modules/document/types";

type DocumentTabsProps = {
  document: GenericDocument;
  versions: GenericDocumentVersion[];
};

export function DocumentTabs({ document, versions }: DocumentTabsProps) {
  const version = document.currentVersion;

  return (
    <Tabs defaultValue="overview">
      <TabsList className="w-full justify-start overflow-x-auto">
        <TabsTrigger value="overview">Overview</TabsTrigger>
        <TabsTrigger value="extracted-text">Extracted Text</TabsTrigger>
        <TabsTrigger value="findings">Findings</TabsTrigger>
        <TabsTrigger value="history">History</TabsTrigger>
      </TabsList>

      <TabsContent value="overview">
        <Card>
          <CardContent className="space-y-3 p-6 text-sm text-muted-foreground">
            <p>
              {document.title} is linked to {formatLabel(document.contextType)} #{document.contextId}. Its current
              version is {version ? `version ${version.versionNumber}` : "not available"}.
            </p>
            <p>Validation and extraction state are loaded from the backend document workflow.</p>
          </CardContent>
        </Card>
      </TabsContent>

      <TabsContent value="extracted-text">
        <Card>
          <CardContent className="p-6">
            <ScrollArea className="max-h-72 rounded-md border bg-muted/30 p-4">
              <p className="text-sm leading-6 text-muted-foreground">
                Extracted text is stored server-side for AI evaluation and is not exposed in the document metadata API.
              </p>
            </ScrollArea>
          </CardContent>
        </Card>
      </TabsContent>

      <TabsContent value="findings">
        <Card>
          <CardContent className="p-6">
            <ul className="list-disc space-y-2 pl-5 text-sm text-muted-foreground">
              <li>Some requirements need measurable acceptance criteria.</li>
              <li>Traceability between design components and requirements can be strengthened.</li>
              <li>Testing references should include negative and boundary scenarios.</li>
            </ul>
          </CardContent>
        </Card>
      </TabsContent>

      <TabsContent value="history">
        <DocumentVersionHistory versions={versions} />
      </TabsContent>
    </Tabs>
  );
}

function formatLabel(value: string) {
  return value
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}
