import type { RouteObject } from "react-router";
import { PlaceholderPage } from "@/common/components/layout/PlaceholderPage";
import { DashboardLayout } from "@/modules/dashboard/components/DashboardLayout";
import { DashboardPage } from "@/modules/dashboard/pages/DashboardPage";
import { documentRoutes } from "@/modules/document/routes";
import { projectRoutes } from "@/modules/project/routes";
import { submissionRoutes } from "@/modules/submission/routes";

export const dashboardRoutes: RouteObject[] = [
  {
    element: <DashboardLayout />,
    children: [
      {
        path: "/dashboard",
        element: <DashboardPage />,
      },
      ...projectRoutes,
      ...submissionRoutes,
      ...documentRoutes,
      {
        path: "/evaluations",
        element: <PlaceholderPage title="Evaluations" />,
      },
      {
        path: "/repository-analysis",
        element: <PlaceholderPage title="Repository Analysis" />,
      },
      {
        path: "/deployment-validation",
        element: <PlaceholderPage title="Deployment Validation" />,
      },
      {
        path: "/reports",
        element: <PlaceholderPage title="Reports" />,
      },
      {
        path: "/settings",
        element: <PlaceholderPage title="Settings" />,
      },
    ],
  },
];
