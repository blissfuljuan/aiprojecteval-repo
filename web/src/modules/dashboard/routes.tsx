import type { RouteObject } from "react-router";
import { PlaceholderPage } from "@/common/components/layout/PlaceholderPage";
import { DashboardLayout } from "@/modules/dashboard/components/DashboardLayout";
import { DashboardPage } from "@/modules/dashboard/pages/DashboardPage";
import { documentRoutes } from "@/modules/document/routes";
import { deploymentValidationRoutes } from "@/modules/deployment-validation/routes";
import { evaluationRoutes } from "@/modules/evaluation/routes";
import { projectRoutes } from "@/modules/project/routes";
import { reportRoutes } from "@/modules/report/routes";
import { repositoryAnalysisRoutes } from "@/modules/repository-analysis/routes";
import { submissionRoutes } from "@/modules/submission/routes";
import { paths } from "@/routes/paths";

export const dashboardRoutes: RouteObject[] = [
  {
    element: <DashboardLayout />,
    children: [
      {
        path: paths.dashboard,
        element: <DashboardPage />,
      },
      ...projectRoutes,
      ...submissionRoutes,
      ...documentRoutes,
      ...evaluationRoutes,
      ...repositoryAnalysisRoutes,
      ...deploymentValidationRoutes,
      ...reportRoutes,
      {
        path: paths.settings,
        element: <PlaceholderPage title="Settings" />,
      },
    ],
  },
];
