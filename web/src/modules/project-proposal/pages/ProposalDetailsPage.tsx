import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { ArrowLeft, Pencil } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Label } from "@/common/ui/shadcn/label";
import { Select } from "@/common/ui/shadcn/select";
import { Separator } from "@/common/ui/shadcn/separator";
import { Textarea } from "@/common/ui/shadcn/textarea";
import { useAuth } from "@/modules/identity/context/AuthContext";
import { ProjectProposalAIEvaluationSection } from "@/modules/project-proposal/components/ProjectProposalAIEvaluationSection";
import { ProjectProposalDocumentSection } from "@/modules/project-proposal/components/ProjectProposalDocumentSection";
import { ProposalStatusBadge } from "@/modules/project-proposal/components/ProposalStatusBadge";
import { useProposal } from "@/modules/project-proposal/hooks/useProposal";
import { projectProposalService } from "@/modules/project-proposal/services/projectProposal.service";
import type { AdviserDecisionRequest, InstructorDecisionRequest, ProposalStatus } from "@/modules/project-proposal/types";
import { paths } from "@/routes/paths";

const EDITABLE_STATUSES: ProposalStatus[] = ["DRAFT", "SUBMITTED", "REVISION_REQUIRED"];
const ADVISER_ACTIONABLE: ProposalStatus[] = ["SUBMITTED", "ADVISER_REVIEW_SCHEDULED"];
const INSTRUCTOR_ACTIONABLE: ProposalStatus[] = [
  "SUBMITTED",
  "ADVISER_REVIEW_SCHEDULED",
  "ADVISER_REVIEWED",
  "INSTRUCTOR_REVIEW_SCHEDULED",
  "REVISION_REQUIRED",
];

export function ProposalDetailsPage() {
  const { proposalId } = useParams<{ proposalId: string }>();
  const { user } = useAuth();
  const navigate = useNavigate();
  const { proposal, isLoading, error, refresh } = useProposal(Number(proposalId));

  const [adviserRemarks, setAdviserRemarks] = useState("");
  const [adviserSubmitting, setAdviserSubmitting] = useState(false);
  const [adviserError, setAdviserError] = useState<string | null>(null);

  const [instructorDecision, setInstructorDecision] = useState<InstructorDecisionRequest["decision"]>("APPROVED");
  const [instructorRemarks, setInstructorRemarks] = useState("");
  const [instructorSubmitting, setInstructorSubmitting] = useState(false);
  const [instructorError, setInstructorError] = useState<string | null>(null);

  async function handleAdviserDecision(decision: AdviserDecisionRequest["decision"]) {
    if (!proposal) return;
    try {
      setAdviserSubmitting(true);
      setAdviserError(null);
      await projectProposalService.adviserDecision(proposal.id, {
        decision,
        remarks: adviserRemarks || undefined,
      });
      await refresh();
      setAdviserRemarks("");
    } catch (err) {
      setAdviserError(projectProposalService.getErrorMessage(err));
    } finally {
      setAdviserSubmitting(false);
    }
  }

  async function handleInstructorDecision(e: React.FormEvent) {
    e.preventDefault();
    if (!proposal) return;
    if (!instructorRemarks.trim()) {
      setInstructorError("Remarks are required.");
      return;
    }
    try {
      setInstructorSubmitting(true);
      setInstructorError(null);
      await projectProposalService.instructorDecision(proposal.id, {
        decision: instructorDecision,
        remarks: instructorRemarks,
      });
      await refresh();
      setInstructorRemarks("");
    } catch (err) {
      setInstructorError(projectProposalService.getErrorMessage(err));
    } finally {
      setInstructorSubmitting(false);
    }
  }

  async function handleDelete() {
    if (!proposal || !confirm("Delete this proposal? This cannot be undone.")) return;
    try {
      await projectProposalService.deleteProposal(proposal.id);
      void navigate(paths.proposals);
    } catch (err) {
      alert(projectProposalService.getErrorMessage(err));
    }
  }

  if (isLoading) {
    return <p className="text-sm text-muted-foreground">Loading proposal...</p>;
  }

  if (error || !proposal) {
    return <p className="text-sm text-destructive">{error ?? "Proposal not found."}</p>;
  }

  const isStudent = user?.role === "STUDENT";
  const isAdviser = user?.role === "ADVISER" || user?.role === "ADMIN";
  const isInstructor = user?.role === "INSTRUCTOR" || user?.role === "ADMIN";
  const canEdit = isStudent && EDITABLE_STATUSES.includes(proposal.status);
  const canDelete = isStudent && (proposal.status === "DRAFT" || proposal.status === "SUBMITTED");
  const showAdviserPanel = isAdviser && ADVISER_ACTIONABLE.includes(proposal.status);
  const showInstructorPanel = isInstructor && INSTRUCTOR_ACTIONABLE.includes(proposal.status);

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-3xl font-semibold tracking-normal">{proposal.title}</h1>
            <ProposalStatusBadge status={proposal.status} />
          </div>
          <p className="text-sm text-muted-foreground">
            {proposal.courseClassName} - Submitted by {proposal.submittedByName}
          </p>
        </div>
        <div className="flex flex-wrap gap-3">
          <Button asChild variant="outline">
            <Link to={paths.proposals}>
              <ArrowLeft className="h-4 w-4" aria-hidden="true" />
              Back
            </Link>
          </Button>
          {canEdit && (
            <Button asChild variant="outline">
              <Link to={paths.proposalEdit(proposal.id)}>
                <Pencil className="h-4 w-4" aria-hidden="true" />
                Edit
              </Link>
            </Button>
          )}
          {canDelete && (
            <Button variant="destructive" type="button" onClick={() => void handleDelete()}>
              Delete
            </Button>
          )}
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Proposal Details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-5">
          <InfoGrid>
            <InfoItem label="Course Class" value={proposal.courseClassName} />
            <InfoItem label="Status" value={<ProposalStatusBadge status={proposal.status} />} />
            <InfoItem label="Submitted By" value={proposal.submittedByName} />
            <InfoItem label="Submitted On" value={new Date(proposal.createdAt).toLocaleDateString()} />
            {proposal.targetUsers && <InfoItem label="Target Users" value={proposal.targetUsers} />}
            {proposal.technologyStack && <InfoItem label="Technology Stack" value={proposal.technologyStack} />}
          </InfoGrid>

          <Separator />

          <Field label="Problem Statement" value={proposal.problemStatement} />
          <Field label="Objectives" value={proposal.objectives} />
          <Field label="Proposed Features" value={proposal.proposedFeatures} />
          {proposal.expectedOutput && <Field label="Expected Output" value={proposal.expectedOutput} />}

          {(proposal.adviserRemarks || proposal.instructorRemarks) && (
            <>
              <Separator />
              {proposal.adviserRemarks && <Field label="Adviser Remarks" value={proposal.adviserRemarks} />}
              {proposal.instructorRemarks && <Field label="Instructor Remarks" value={proposal.instructorRemarks} />}
            </>
          )}

          {proposal.approvedAt && (
            <p className="text-sm text-muted-foreground">
              Approved on {new Date(proposal.approvedAt).toLocaleDateString()}
            </p>
          )}
          {proposal.rejectedAt && (
            <p className="text-sm text-muted-foreground">
              Rejected on {new Date(proposal.rejectedAt).toLocaleDateString()}
            </p>
          )}
          {proposal.revisionRequestedAt && (
            <p className="text-sm text-muted-foreground">
              Revision requested on {new Date(proposal.revisionRequestedAt).toLocaleDateString()}
            </p>
          )}
        </CardContent>
      </Card>

      <ProjectProposalDocumentSection
        proposalId={proposal.id}
        proposalTitle={proposal.title}
        canManage={canEdit}
        onDocumentChanged={refresh}
      />

      <ProjectProposalAIEvaluationSection proposalId={proposal.id} canEvaluate={isAdviser || isInstructor} />

      {showAdviserPanel && (
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Adviser Review</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {adviserError && <p className="text-sm text-destructive">{adviserError}</p>}
            <div className="grid gap-2">
              <Label htmlFor="adviser-remarks">Remarks (optional)</Label>
              <Textarea
                id="adviser-remarks"
                placeholder="Add your review remarks..."
                rows={3}
                value={adviserRemarks}
                onChange={(e) => setAdviserRemarks(e.target.value)}
              />
            </div>
          </CardContent>
          <CardFooter className="gap-3">
            <Button
              type="button"
              variant="outline"
              disabled={adviserSubmitting}
              onClick={() => void handleAdviserDecision("ADVISER_REVIEW_SCHEDULED")}
            >
              Schedule Review
            </Button>
            <Button
              type="button"
              disabled={adviserSubmitting}
              onClick={() => void handleAdviserDecision("ADVISER_REVIEWED")}
            >
              {adviserSubmitting ? "Saving..." : "Mark as Reviewed"}
            </Button>
          </CardFooter>
        </Card>
      )}

      {showInstructorPanel && (
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Instructor Decision</CardTitle>
          </CardHeader>
          <form onSubmit={(e) => void handleInstructorDecision(e)}>
            <CardContent className="space-y-4">
              {instructorError && <p className="text-sm text-destructive">{instructorError}</p>}
              <div className="grid gap-2">
                <Label htmlFor="instructor-decision">Decision</Label>
                <Select
                  id="instructor-decision"
                  value={instructorDecision}
                  onChange={(e) => setInstructorDecision(e.target.value as InstructorDecisionRequest["decision"])}
                >
                  <option value="APPROVED">Approve</option>
                  <option value="REVISION_REQUIRED">Request Revision</option>
                  <option value="REJECTED">Reject</option>
                </Select>
              </div>
              <div className="grid gap-2">
                <Label htmlFor="instructor-remarks">Remarks <span aria-hidden="true">*</span></Label>
                <Textarea
                  id="instructor-remarks"
                  placeholder="Provide your remarks for this decision..."
                  rows={3}
                  value={instructorRemarks}
                  onChange={(e) => setInstructorRemarks(e.target.value)}
                  required
                />
              </div>
            </CardContent>
            <CardFooter>
              <Button type="submit" disabled={instructorSubmitting}>
                {instructorSubmitting ? "Submitting..." : "Submit Decision"}
              </Button>
            </CardFooter>
          </form>
        </Card>
      )}
    </div>
  );
}

function InfoGrid({ children }: { children: React.ReactNode }) {
  return <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">{children}</div>;
}

function InfoItem({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className="rounded-md border bg-muted/30 p-4">
      <p className="text-xs font-medium uppercase tracking-normal text-muted-foreground">{label}</p>
      <div className="mt-1 text-sm font-medium">{value}</div>
    </div>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div className="space-y-1">
      <p className="text-xs font-medium uppercase tracking-normal text-muted-foreground">{label}</p>
      <p className="text-sm leading-6 whitespace-pre-wrap">{value}</p>
    </div>
  );
}
