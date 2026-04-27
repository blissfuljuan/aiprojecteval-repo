import { Link } from "react-router";
import { Eye, Plus } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";
import { useAuth } from "@/modules/identity/context/AuthContext";
import { ProposalStatusBadge } from "@/modules/project-proposal/components/ProposalStatusBadge";
import { useProposals } from "@/modules/project-proposal/hooks/useProposals";
import { paths } from "@/routes/paths";

export function ProposalListPage() {
  const { user } = useAuth();
  const { proposals, isLoading, error } = useProposals();

  const isStudent = user?.role === "STUDENT";
  const heading = isStudent ? "My Proposals" : "All Proposals";
  const description = isStudent
    ? "Track the status of your submitted project proposals."
    : "Review and manage submitted project proposals.";

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">{heading}</h1>
          <p className="text-sm text-muted-foreground">{description}</p>
        </div>
        {isStudent && (
          <Button asChild>
            <Link to={paths.proposalCreate}>
              <Plus className="h-4 w-4" aria-hidden="true" />
              New Proposal
            </Link>
          </Button>
        )}
      </div>

      <Card>
        <CardHeader className="pb-4">
          <CardTitle className="text-lg">Proposals</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <Separator />

          {error && (
            <p className="text-sm text-destructive">{error}</p>
          )}

          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading proposals...</p>
          ) : proposals.length === 0 ? (
            <p className="text-sm text-muted-foreground">
              {isStudent ? "You have not submitted any proposals yet." : "No proposals found."}
            </p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Title</TableHead>
                  <TableHead>Course Class</TableHead>
                  {!isStudent && <TableHead>Submitted By</TableHead>}
                  <TableHead>Status</TableHead>
                  <TableHead>Submitted On</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {proposals.map((proposal) => (
                  <TableRow key={proposal.id}>
                    <TableCell className="font-medium">{proposal.title}</TableCell>
                    <TableCell>{proposal.courseClassName}</TableCell>
                    {!isStudent && <TableCell>{proposal.submittedByName}</TableCell>}
                    <TableCell>
                      <ProposalStatusBadge status={proposal.status} />
                    </TableCell>
                    <TableCell>{new Date(proposal.createdAt).toLocaleDateString()}</TableCell>
                    <TableCell>
                      <div className="flex justify-end">
                        <Button size="sm" variant="outline" asChild>
                          <Link to={paths.proposalDetails(proposal.id)}>
                            <Eye className="h-3.5 w-3.5" aria-hidden="true" />
                            View
                          </Link>
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
