import { createBrowserRouter } from "react-router";
import { AuthLayout } from "@/common/components/layout/AuthLayout";
import { PublicLayout } from "@/common/components/layout/PublicLayout";
import { dashboardRoutes } from "@/modules/dashboard/routes";
import { NotFoundPage } from "@/modules/errors/pages/NotFoundPage";
import { HomePage } from "@/modules/home/pages/HomePage";
import { LoginPage } from "@/modules/identity/pages/LoginPage";
import { RegisterPage } from "@/modules/identity/pages/RegisterPage";
import { ProtectedRoute } from "@/routes/ProtectedRoute";
import { PublicOnlyRoute } from "@/routes/PublicOnlyRoute";
import { paths } from "@/routes/paths";

export const router = createBrowserRouter([
  {
    path: paths.home,
    element: <PublicLayout />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
      {
        element: <PublicOnlyRoute />,
        children: [
          {
            element: <AuthLayout />,
            children: [
              {
                path: paths.login,
                element: <LoginPage />,
              },
              {
                path: paths.register,
                element: <RegisterPage />,
              },
            ],
          },
        ],
      },
    ],
  },
  {
    element: <ProtectedRoute />,
    children: dashboardRoutes,
  },
  {
    path: "*",
    element: <NotFoundPage />,
  },
]);
