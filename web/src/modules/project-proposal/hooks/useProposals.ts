import { useEffect, useState } from "react";
import { useAuth } from "@/modules/identity/context/AuthContext";
import { projectProposalService } from "@/modules/project-proposal/services/projectProposal.service";
import type { ProjectProposal } from "@/modules/project-proposal/types";

export function useProposals() {
  const { user } = useAuth();
  const [proposals, setProposals] = useState<ProjectProposal[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!user) return;

    const fetch = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const data =
          user.role === "STUDENT"
            ? await projectProposalService.getMyProposals()
            : await projectProposalService.getAllProposals();
        setProposals(data);
      } catch (err) {
        setError(projectProposalService.getErrorMessage(err));
      } finally {
        setIsLoading(false);
      }
    };

    void fetch();
  }, [user]);

  return { proposals, isLoading, error };
}
