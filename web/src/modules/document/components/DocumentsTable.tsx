import { Link } from "react-router";
import { BarChart3, Eye } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { ScrollArea } from "@/common/ui/shadcn/scroll-area";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";
import { DocumentStatusBadge } from "@/modules/document/components/DocumentStatusBadge";
import type { UploadedDocument } from "@/modules/document/types";

type DocumentsTableProps = {
  documents: UploadedDocument[];
};

export function DocumentsTable({ documents }: DocumentsTableProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Submitted Documents</CardTitle>
      </CardHeader>
      <CardContent>
        <ScrollArea className="w-full">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>File Name</TableHead>
                <TableHead>Document Type</TableHead>
                <TableHead>Project</TableHead>
                <TableHead>Submission Version</TableHead>
                <TableHead>Uploaded By</TableHead>
                <TableHead>Uploaded Date</TableHead>
                <TableHead>Analysis Status</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {documents.map((document) => (
                <TableRow key={document.id}>
                  <TableCell className="min-w-[220px] font-medium">{document.fileName}</TableCell>
                  <TableCell>{document.documentType}</TableCell>
                  <TableCell className="min-w-[220px]">{document.projectName}</TableCell>
                  <TableCell>{document.submissionVersion}</TableCell>
                  <TableCell className="min-w-[140px]">{document.uploadedBy}</TableCell>
                  <TableCell className="min-w-[120px]">{document.uploadedAt}</TableCell>
                  <TableCell>
                    <DocumentStatusBadge status={document.status} />
                  </TableCell>
                  <TableCell>
                    <div className="flex justify-end gap-2">
                      <Button asChild variant="ghost" size="sm">
                        <Link to={`/documents/${document.id}`}>
                          <Eye className="h-4 w-4" aria-hidden="true" />
                          View Details
                        </Link>
                      </Button>
                      <Button asChild variant="ghost" size="sm">
                        <Link to={`/documents/${document.id}/analysis`}>
                          <BarChart3 className="h-4 w-4" aria-hidden="true" />
                          View Analysis
                        </Link>
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </ScrollArea>
      </CardContent>
    </Card>
  );
}
