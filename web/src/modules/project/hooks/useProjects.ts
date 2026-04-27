import { useEffect, useState } from "react";
import { projectService } from "@/modules/project/services/project.service";
import type { Project } from "@/modules/project/types";

export function useProjects() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetch = async () => {
      try {
        setIsLoading(true);
        setError(null);
        setProjects(await projectService.findAll());
      } catch (err) {
        setError(projectService.getErrorMessage(err));
      } finally {
        setIsLoading(false);
      }
    };

    void fetch();
  }, []);

  return { projects, isLoading, error };
}
