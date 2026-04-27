import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/common/ui/shadcn/tabs";
import { CommitHistoryPanel } from "@/modules/repository-analysis/components/CommitHistoryPanel";
import { DetectedModulesPanel } from "@/modules/repository-analysis/components/DetectedModulesPanel";
import { FileStructurePanel } from "@/modules/repository-analysis/components/FileStructurePanel";
import { IssuesFoundPanel } from "@/modules/repository-analysis/components/IssuesFoundPanel";

export function RepositoryAnalysisTabs() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Analysis Results</CardTitle>
        <CardDescription>Static repository analysis placeholders for layout validation.</CardDescription>
      </CardHeader>
      <CardContent>
        <Tabs defaultValue="file-structure">
          <TabsList className="grid h-auto w-full grid-cols-2 gap-1 lg:grid-cols-4">
            <TabsTrigger value="file-structure">File Structure</TabsTrigger>
            <TabsTrigger value="detected-modules">Detected Modules</TabsTrigger>
            <TabsTrigger value="commit-history">Commit History</TabsTrigger>
            <TabsTrigger value="issues-found">Issues Found</TabsTrigger>
          </TabsList>
          <TabsContent value="file-structure">
            <FileStructurePanel />
          </TabsContent>
          <TabsContent value="detected-modules">
            <DetectedModulesPanel />
          </TabsContent>
          <TabsContent value="commit-history">
            <CommitHistoryPanel />
          </TabsContent>
          <TabsContent value="issues-found">
            <IssuesFoundPanel />
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  );
}
