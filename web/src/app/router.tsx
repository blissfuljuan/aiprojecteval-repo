import { createBrowserRouter } from "react-router";
import { AuthLayout } from "@/common/components/layout/AuthLayout";
import { PublicLayout } from "@/common/components/layout/PublicLayout";
import { RequireAuth } from "@/common/guards/RequireAuth";
import { RequireRole } from "@/common/guards/RequireRole";
import { dashboardRoutes } from "@/modules/dashboard/routes";
import { deploymentValidationRoutes } from "@/modules/deployment-validation/routes";
import { documentRoutes } from "@/modules/document/routes";
import { evaluationRoutes } from "@/modules/evaluation/routes";
import { HomePage } from "@/modules/home/pages/HomePage";
import { LoginPage } from "@/modules/identity/pages/LoginPage";
import { ProfilePage } from "@/modules/identity/pages/ProfilePage";
import { RegisterPage } from "@/modules/identity/pages/RegisterPage";
import { projectRoutes } from "@/modules/project/routes";
import { reportRoutes } from "@/modules/report/routes";
import { repositoryAnalysisRoutes } from "@/modules/repository-analysis/routes";
import { submissionRoutes } from "@/modules/submission/routes";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <PublicLayout />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
    ],
  },
  {
    element: <AuthLayout />,
    children: [
      {
        path: "/login",
        element: <LoginPage />,
      },
      {
        path: "/register",
        element: <RegisterPage />,
      },
    ],
  },
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
        path: "/profile",
        element: <ProfilePage />,
      },
      {
        element: <RequireRole allowedRoles={["admin", "evaluator"]} />,
        children: [],
      },
    ],
  },
]);
