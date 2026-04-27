import { Filter, RefreshCw, Search } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Select } from "@/common/ui/shadcn/select";
import { DocumentSummaryCards } from "@/modules/document/components/DocumentSummaryCards";
import { DocumentsTable } from "@/modules/document/components/DocumentsTable";
import { useDocuments } from "@/modules/document/hooks/useDocuments";

export function DocumentListPage() {
  const { documents, totalDocuments, pendingAnalysis, analyzed, needsReview } = useDocuments();

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Documents</h1>
          <p className="text-sm text-muted-foreground">
            Review and monitor submitted software engineering documents.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button variant="outline">
            <Filter className="h-4 w-4" aria-hidden="true" />
            Filter
          </Button>
          <Button variant="outline">
            <RefreshCw className="h-4 w-4" aria-hidden="true" />
            Refresh
          </Button>
        </div>
      </div>

      <DocumentSummaryCards
        totalDocuments={totalDocuments}
        pendingAnalysis={pendingAnalysis}
        analyzed={analyzed}
        needsReview={needsReview}
      />

      <Card>
        <CardContent className="grid gap-4 p-4 md:grid-cols-2 xl:grid-cols-4">
          <div className="relative">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input placeholder="Search documents" className="pl-9" />
          </div>
          <Select defaultValue="all-types" aria-label="Document type filter">
            <option value="all-types">All document types</option>
            <option value="srs">SRS</option>
            <option value="sdd">SDD</option>
            <option value="spmp">SPMP</option>
            <option value="std">STD</option>
          </Select>
          <Select defaultValue="all-statuses" aria-label="Status filter">
            <option value="all-statuses">All statuses</option>
            <option value="uploaded">Uploaded</option>
            <option value="pending-analysis">Pending Analysis</option>
            <option value="analyzed">Analyzed</option>
            <option value="needs-review">Needs Review</option>
            <option value="failed">Failed</option>
          </Select>
          <Select defaultValue="all-projects" aria-label="Project filter">
            <option value="all-projects">All projects</option>
            <option value="campus-clinic">Campus Clinic Appointment System</option>
            <option value="library-kiosk">Library Kiosk Reservation System</option>
            <option value="inventory-audit">Inventory Audit Tracker</option>
            <option value="event-rsvp">Event RSVP System</option>
          </Select>
        </CardContent>
      </Card>

      <DocumentsTable documents={documents} />
    </div>
  );
}
