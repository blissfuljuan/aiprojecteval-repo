import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Textarea } from "@/common/ui/shadcn/textarea";
import { ProjectProposalDocumentSection } from "@/modules/project-proposal/components/ProjectProposalDocumentSection";
import { useProposal } from "@/modules/project-proposal/hooks/useProposal";
import { projectProposalService } from "@/modules/project-proposal/services/projectProposal.service";
import type { ProposalUpdateRequest } from "@/modules/project-proposal/types";
import { paths } from "@/routes/paths";

export function ProposalEditPage() {
  const { proposalId } = useParams<{ proposalId: string }>();
  const navigate = useNavigate();
  const { proposal, isLoading, error } = useProposal(Number(proposalId));

  const [form, setForm] = useState<ProposalUpdateRequest>({
    title: "",
    problemStatement: "",
    objectives: "",
    targetUsers: "",
    proposedFeatures: "",
    technologyStack: "",
    expectedOutput: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  useEffect(() => {
    if (!proposal) return;
    setForm({
      title: proposal.title,
      problemStatement: proposal.problemStatement,
      objectives: proposal.objectives,
      targetUsers: proposal.targetUsers ?? "",
      proposedFeatures: proposal.proposedFeatures,
      technologyStack: proposal.technologyStack ?? "",
      expectedOutput: proposal.expectedOutput ?? "",
    });
  }, [proposal]);

  function set(field: keyof ProposalUpdateRequest, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!proposal) return;
    try {
      setIsSubmitting(true);
      setSubmitError(null);
      await projectProposalService.updateProposal(proposal.id, form);
      void navigate(paths.proposalDetails(proposal.id));
    } catch (err) {
      setSubmitError(projectProposalService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  if (isLoading) return <p className="text-sm text-muted-foreground">Loading...</p>;
  if (error || !proposal) return <p className="text-sm text-destructive">{error ?? "Proposal not found."}</p>;

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Edit Proposal</h1>
          <p className="text-sm text-muted-foreground">Update your project proposal details.</p>
        </div>
        <Button asChild variant="outline">
          <Link to={paths.proposalDetails(proposal.id)}>
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            Back to Proposal
          </Link>
        </Button>
      </div>

      <form onSubmit={(e) => void handleSubmit(e)}>
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Proposal Details</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-5">
            {submitError && <p className="text-sm text-destructive">{submitError}</p>}

            <div className="grid gap-2">
              <Label htmlFor="title">Project Title <span aria-hidden="true">*</span></Label>
              <Input
                id="title"
                value={form.title}
                onChange={(e) => set("title", e.target.value)}
                required
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="problem-statement">Problem Statement <span aria-hidden="true">*</span></Label>
              <Textarea
                id="problem-statement"
                rows={4}
                value={form.problemStatement}
                onChange={(e) => set("problemStatement", e.target.value)}
                required
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="objectives">Objectives <span aria-hidden="true">*</span></Label>
              <Textarea
                id="objectives"
                rows={3}
                value={form.objectives}
                onChange={(e) => set("objectives", e.target.value)}
                required
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="proposed-features">Proposed Features <span aria-hidden="true">*</span></Label>
              <Textarea
                id="proposed-features"
                rows={3}
                value={form.proposedFeatures}
                onChange={(e) => set("proposedFeatures", e.target.value)}
                required
              />
            </div>

            <div className="grid gap-5 md:grid-cols-2">
              <div className="grid gap-2">
                <Label htmlFor="target-users">Target Users</Label>
                <Input
                  id="target-users"
                  value={form.targetUsers}
                  onChange={(e) => set("targetUsers", e.target.value)}
                />
              </div>

              <div className="grid gap-2">
                <Label htmlFor="tech-stack">Technology Stack</Label>
                <Input
                  id="tech-stack"
                  value={form.technologyStack}
                  onChange={(e) => set("technologyStack", e.target.value)}
                />
              </div>
            </div>

            <div className="grid gap-2">
              <Label htmlFor="expected-output">Expected Output</Label>
              <Textarea
                id="expected-output"
                rows={3}
                value={form.expectedOutput}
                onChange={(e) => set("expectedOutput", e.target.value)}
              />
            </div>
          </CardContent>
          <CardFooter className="justify-end gap-3">
            <Button asChild variant="outline">
              <Link to={paths.proposalDetails(proposal.id)}>Cancel</Link>
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Saving..." : "Save Changes"}
            </Button>
          </CardFooter>
        </Card>
      </form>

      <ProjectProposalDocumentSection proposalId={proposal.id} proposalTitle={proposal.title} canManage />
    </div>
  );
}
