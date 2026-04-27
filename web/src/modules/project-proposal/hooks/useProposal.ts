import { useCallback, useEffect, useState } from "react";
import { projectProposalService } from "@/modules/project-proposal/services/projectProposal.service";
import type { ProjectProposal } from "@/modules/project-proposal/types";

export function useProposal(id: number) {
  const [proposal, setProposal] = useState<ProjectProposal | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await projectProposalService.getProposalById(id);
      setProposal(data);
    } catch (err) {
      setError(projectProposalService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }, [id]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  return { proposal, isLoading, error, refresh };
}
