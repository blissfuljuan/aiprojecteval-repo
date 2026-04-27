import { Card, CardContent } from "@/common/ui/shadcn/card";
import { ScrollArea } from "@/common/ui/shadcn/scroll-area";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/common/ui/shadcn/tabs";
import { DocumentVersionHistory } from "@/modules/document/components/DocumentVersionHistory";
import type { UploadedDocument } from "@/modules/document/types";

type DocumentTabsProps = {
  document: UploadedDocument;
};

export function DocumentTabs({ document }: DocumentTabsProps) {
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
              {document.fileName} is part of {document.projectName} submission {document.submissionVersion}. This
              placeholder summarizes lifecycle state after submission without loading document content from an API.
            </p>
            <p>The module will later support read-only inspection, re-analysis tracking, and document history review.</p>
          </CardContent>
        </Card>
      </TabsContent>

      <TabsContent value="extracted-text">
        <Card>
          <CardContent className="p-6">
            <ScrollArea className="max-h-72 rounded-md border bg-muted/30 p-4">
              <p className="text-sm leading-6 text-muted-foreground">
                Placeholder extracted text: The system shall allow authorized evaluators to inspect submitted project
                documentation. The document shall include functional requirements, non-functional requirements, system
                design references, test coverage notes, and compliance traceability sections.
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
        <DocumentVersionHistory />
      </TabsContent>
    </Tabs>
  );
}
