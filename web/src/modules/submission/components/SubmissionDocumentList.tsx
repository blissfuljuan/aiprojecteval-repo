import { Eye, MoreHorizontal } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";
import type { SubmissionDocument } from "@/modules/submission/types";

const documents: SubmissionDocument[] = [
  { id: "doc-1", type: "SRS", fileName: "campus-clinic-srs.pdf", status: "Uploaded", uploadedAt: "Apr 24, 2026" },
  { id: "doc-2", type: "SDD", fileName: "campus-clinic-sdd.pdf", status: "Uploaded", uploadedAt: "Apr 24, 2026" },
  { id: "doc-3", type: "SPMP", fileName: "campus-clinic-spmp.pdf", status: "Uploaded", uploadedAt: "Apr 25, 2026" },
  { id: "doc-4", type: "STD", fileName: "campus-clinic-std.pdf", status: "Uploaded", uploadedAt: "Apr 25, 2026" },
];

export function SubmissionDocumentList() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Document List</CardTitle>
        <CardDescription>Submitted project documentation package.</CardDescription>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Document Type</TableHead>
              <TableHead>File Name</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Uploaded At</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {documents.map((document) => (
              <TableRow key={document.id}>
                <TableCell className="font-medium">{document.type}</TableCell>
                <TableCell>{document.fileName}</TableCell>
                <TableCell>
                  <Badge variant="outline" className="border-emerald-200 bg-emerald-50 text-emerald-700">
                    {document.status}
                  </Badge>
                </TableCell>
                <TableCell>{document.uploadedAt}</TableCell>
                <TableCell className="text-right">
                  <div className="flex justify-end gap-2">
                    <Button type="button" variant="ghost" size="icon" aria-label="View document">
                      <Eye className="h-4 w-4" aria-hidden="true" />
                    </Button>
                    <Button type="button" variant="ghost" size="icon" aria-label="More document actions">
                      <MoreHorizontal className="h-4 w-4" aria-hidden="true" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
