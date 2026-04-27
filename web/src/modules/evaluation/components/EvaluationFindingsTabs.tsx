import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/common/ui/shadcn/tabs";
import { Textarea } from "@/common/ui/shadcn/textarea";

const findings = [
  "SRS contains functional requirements but some acceptance criteria are missing.",
  "SDD architecture section is present but database design needs refinement.",
  "Repository structure partially matches the documented architecture.",
  "Deployment URL is reachable.",
];

const requirements = [
  {
    id: "REQ-001",
    description: "Users can manage clinic appointments.",
    status: "Compliant",
    evidence: "SRS section 3.1, repository routes",
  },
  {
    id: "REQ-002",
    description: "Administrators can review appointment reports.",
    status: "Partial",
    evidence: "Dashboard screenshots, missing acceptance criteria",
  },
  {
    id: "REQ-003",
    description: "System deployment is accessible for evaluation.",
    status: "Compliant",
    evidence: "Deployment URL check",
  },
];

const documents = [
  {
    document: "Software Requirements Specification",
    status: "Submitted",
    notes: "Functional requirements present; acceptance criteria need more detail.",
  },
  {
    document: "Software Design Document",
    status: "Submitted",
    notes: "Architecture section present; database design needs refinement.",
  },
  {
    document: "Deployment Guide",
    status: "Submitted",
    notes: "URL and setup notes available.",
  },
];

export function EvaluationFindingsTabs() {
  return (
    <Tabs defaultValue="summary">
      <div className="overflow-x-auto">
        <TabsList className="w-max">
          <TabsTrigger value="summary">Summary</TabsTrigger>
          <TabsTrigger value="findings">Findings</TabsTrigger>
          <TabsTrigger value="requirements">Requirements</TabsTrigger>
          <TabsTrigger value="documents">Documents</TabsTrigger>
          <TabsTrigger value="remarks">Instructor Remarks</TabsTrigger>
        </TabsList>
      </div>

      <TabsContent value="summary">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">AI-Assisted Evaluation Summary</CardTitle>
            <CardDescription>Static placeholder summary for the selected evaluation.</CardDescription>
          </CardHeader>
          <CardContent className="text-sm leading-6 text-muted-foreground">
            The submission demonstrates strong documentation coverage and a reachable deployment. Requirement evidence is
            mostly traceable across documents and repository structure, with minor gaps in acceptance criteria and database
            design detail.
          </CardContent>
        </Card>
      </TabsContent>

      <TabsContent value="findings">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Findings</CardTitle>
            <CardDescription>Static findings prepared for instructor review.</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {findings.map((finding, index) => (
                <div key={finding} className="rounded-md border p-4">
                  <div className="flex gap-3">
                    <Badge variant="secondary" className="h-6 min-w-6 justify-center px-2">
                      {index + 1}
                    </Badge>
                    <p className="text-sm leading-6">{finding}</p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </TabsContent>

      <TabsContent value="requirements">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Requirements</CardTitle>
            <CardDescription>Placeholder traceability table.</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Requirement ID</TableHead>
                    <TableHead>Description</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Evidence</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {requirements.map((requirement) => (
                    <TableRow key={requirement.id}>
                      <TableCell className="font-medium">{requirement.id}</TableCell>
                      <TableCell>{requirement.description}</TableCell>
                      <TableCell>
                        <Badge variant="outline">{requirement.status}</Badge>
                      </TableCell>
                      <TableCell>{requirement.evidence}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>
      </TabsContent>

      <TabsContent value="documents">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Documents</CardTitle>
            <CardDescription>Placeholder document review table.</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Document</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Notes</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {documents.map((document) => (
                    <TableRow key={document.document}>
                      <TableCell className="font-medium">{document.document}</TableCell>
                      <TableCell>
                        <Badge variant="outline">{document.status}</Badge>
                      </TableCell>
                      <TableCell>{document.notes}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>
      </TabsContent>

      <TabsContent value="remarks">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Instructor Remarks</CardTitle>
            <CardDescription>Static placeholder for evaluator notes.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <Textarea placeholder="Add instructor comments or revision notes..." />
            <Separator />
            <p className="text-sm text-muted-foreground">Comments are not saved yet. This area is static for layout review.</p>
          </CardContent>
        </Card>
      </TabsContent>
    </Tabs>
  );
}
