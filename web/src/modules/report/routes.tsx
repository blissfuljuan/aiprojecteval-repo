import type { RouteObject } from "react-router";
import { ReportDetailsPage } from "@/modules/report/pages/ReportDetailsPage";
import { ReportListPage } from "@/modules/report/pages/ReportListPage";

export const reportRoutes: RouteObject[] = [
  {
    path: "/reports",
    element: <ReportListPage />,
  },
  {
    path: "/reports/:reportId",
    element: <ReportDetailsPage />,
  },
];
