import type { RouteObject } from "react-router";
import { RequireRole } from "@/common/guards/RequireRole";
import {
  courseClassManagementRoles,
  studentModuleRoles,
  studentOnlyRoles,
  unrestrictedRoles,
} from "@/common/lib/roleAccess";
import { PlaceholderPage } from "@/common/components/layout/PlaceholderPage";
import { courseClassRoutes } from "@/modules/course-class/routes";
import { DashboardLayout } from "@/modules/dashboard/components/DashboardLayout";
import { DashboardPage } from "@/modules/dashboard/pages/DashboardPage";
import { documentRoutes } from "@/modules/document/routes";
import { deploymentValidationRoutes } from "@/modules/deployment-validation/routes";
import { evaluationRoutes } from "@/modules/evaluation/routes";
import { projectRoutes } from "@/modules/project/routes";
import { proposalRoutes } from "@/modules/project-proposal/routes";
import { reportRoutes } from "@/modules/report/routes";
import { repositoryAnalysisRoutes } from "@/modules/repository-analysis/routes";
import { studentCourseClassRoutes } from "@/modules/student-course-class/routes";
import { submissionRoutes } from "@/modules/submission/routes";
import { paths } from "@/routes/paths";

export const dashboardRoutes: RouteObject[] = [
  {
    element: <DashboardLayout />,
    children: [
      {
        element: <RequireRole allowedRoles={unrestrictedRoles} />,
        children: [
          {
            path: paths.dashboard,
            element: <DashboardPage />,
          },
        ],
      },
      {
        element: <RequireRole allowedRoles={courseClassManagementRoles} />,
        children: [
          ...courseClassRoutes,
        ],
      },
      {
        element: <RequireRole allowedRoles={studentOnlyRoles} />,
        children: [
          ...studentCourseClassRoutes,
        ],
      },
      {
        element: <RequireRole allowedRoles={studentModuleRoles} />,
        children: [
          ...projectRoutes,
          ...proposalRoutes,
          ...submissionRoutes,
          ...repositoryAnalysisRoutes,
          ...deploymentValidationRoutes,
        ],
      },
      {
        element: <RequireRole allowedRoles={unrestrictedRoles} />,
        children: [
          ...documentRoutes,
          ...evaluationRoutes,
          ...reportRoutes,
          {
            path: paths.settings,
            element: <PlaceholderPage title="Settings" />,
          },
        ],
      },
    ],
  },
];
