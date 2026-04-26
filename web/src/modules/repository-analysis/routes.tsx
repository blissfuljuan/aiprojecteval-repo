import type { RouteObject } from "react-router";
import { RepositoryAnalysisPage } from "@/modules/repository-analysis/pages/RepositoryAnalysisPage";

export const repositoryAnalysisRoutes: RouteObject[] = [
  {
    path: "/repository-analysis",
    element: <RepositoryAnalysisPage />,
  },
];
