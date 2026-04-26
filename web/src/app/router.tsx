import { createBrowserRouter } from "react-router";
import { AuthLayout } from "@/common/components/layout/AuthLayout";
import { PublicLayout } from "@/common/components/layout/PublicLayout";
import { RequireAuth } from "@/common/guards/RequireAuth";
import { HomePage } from "@/modules/home/pages/HomePage";
import { LoginPage } from "@/modules/identity/pages/LoginPage";
import { RegisterPage } from "@/modules/identity/pages/RegisterPage";
import { dashboardRoutes } from "@/modules/dashboard/routes";

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
    children: dashboardRoutes,
  },
]);
