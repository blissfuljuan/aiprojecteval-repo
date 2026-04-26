import { Navigate, createBrowserRouter } from "react-router";
import { RequireAuth } from "@/common/guards/RequireAuth";
import { RequireRole } from "@/common/guards/RequireRole";
import { dashboardRoutes } from "@/modules/dashboard/routes";
import { deploymentValidationRoutes } from "@/modules/deployment-validation/routes";
import { documentRoutes } from "@/modules/document/routes";
import { evaluationRoutes } from "@/modules/evaluation/routes";
import { identityRoutes } from "@/modules/identity/routes";
import { projectRoutes } from "@/modules/project/routes";
import { reportRoutes } from "@/modules/report/routes";
import { repositoryAnalysisRoutes } from "@/modules/repository-analysis/routes";
import { submissionRoutes } from "@/modules/submission/routes";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/dashboard" replace />,
  },
  ...identityRoutes,
  {
    element: <RequireAuth />,
    children: [
      ...dashboardRoutes,
      ...projectRoutes,
      ...submissionRoutes,
      ...documentRoutes,
      ...evaluationRoutes,
      ...repositoryAnalysisRoutes,
      ...deploymentValidationRoutes,
      ...reportRoutes,
      {
        element: <RequireRole allowedRoles={["admin", "evaluator"]} />,
        children: [],
      },
    ],
  },
]);
