import { FileText, Upload } from "lucide-react";
import { Alert, AlertDescription, AlertTitle } from "@/common/ui/shadcn/alert";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const documents = [
  { type: "SRS", title: "Software Requirements Specification", status: "Required" },
  { type: "SDD", title: "Software Design Document", status: "Pending" },
  { type: "SPMP", title: "Software Project Management Plan", status: "Pending" },
  { type: "STD", title: "Software Test Documentation", status: "Uploaded" },
];

export function SubmissionDocumentsStep() {
  return (
    <div className="grid gap-4">
      <Alert className="border-blue-200 bg-blue-50 text-blue-900">
        <AlertTitle>Required document set</AlertTitle>
        <AlertDescription>Upload cards are static placeholders for layout visualization only.</AlertDescription>
      </Alert>
      <div className="grid gap-4 md:grid-cols-2">
        {documents.map((document) => (
          <Card key={document.type} className="shadow-none">
            <CardHeader className="space-y-3">
              <div className="flex items-start justify-between gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-md bg-muted">
                  <FileText className="h-5 w-5 text-muted-foreground" aria-hidden="true" />
                </div>
                <Badge variant={document.status === "Uploaded" ? "default" : "secondary"}>{document.status}</Badge>
              </div>
              <div>
                <CardTitle className="text-base">{document.type}</CardTitle>
                <CardDescription>{document.title}</CardDescription>
              </div>
            </CardHeader>
            <CardContent>
              <Button type="button" variant="outline" className="w-full">
                <Upload className="h-4 w-4" aria-hidden="true" />
                Upload Document
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
