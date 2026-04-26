import type { RouteObject } from "react-router";
import { DashboardPage } from "@/modules/dashboard/pages/DashboardPage";

export const dashboardRoutes: RouteObject[] = [
  {
    path: "/dashboard",
    element: <DashboardPage />,
  },
];
