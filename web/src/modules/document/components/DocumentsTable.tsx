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
import type { GenericDocumentSummary } from "@/modules/document/types";
import { paths } from "@/routes/paths";

type DocumentsTableProps = {
  documents: GenericDocumentSummary[];
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
                <TableHead>Title</TableHead>
                <TableHead>Document Type</TableHead>
                <TableHead>Context</TableHead>
                <TableHead>Version</TableHead>
                <TableHead>Validation</TableHead>
                <TableHead>Extraction</TableHead>
                <TableHead>Updated</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {documents.map((document) => (
                <TableRow key={document.id}>
                  <TableCell className="min-w-[220px] font-medium">{document.title}</TableCell>
                  <TableCell>{formatLabel(document.documentType)}</TableCell>
                  <TableCell className="min-w-[160px]">{formatLabel(document.contextType)} #{document.contextId}</TableCell>
                  <TableCell>{document.currentVersionNumber ? `Version ${document.currentVersionNumber}` : "None"}</TableCell>
                  <TableCell>
                    <DocumentStatusBadge status={document.validationStatus} />
                  </TableCell>
                  <TableCell>
                    <DocumentStatusBadge status={document.extractionStatus} />
                  </TableCell>
                  <TableCell className="min-w-[120px]">{new Date(document.updatedAt).toLocaleDateString()}</TableCell>
                  <TableCell>
                    <div className="flex justify-end gap-2">
                      <Button asChild variant="ghost" size="sm">
                        <Link to={paths.documentDetails(document.id)}>
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

function formatLabel(value: string) {
  return value
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}
