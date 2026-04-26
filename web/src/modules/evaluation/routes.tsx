import type { RouteObject } from "react-router";
import { EvaluationDashboardPage } from "@/modules/evaluation/pages/EvaluationDashboardPage";
import { EvaluationDetailsPage } from "@/modules/evaluation/pages/EvaluationDetailsPage";

export const evaluationRoutes: RouteObject[] = [
  {
    path: "/evaluations",
    element: <EvaluationDashboardPage />,
  },
  {
    path: "/evaluations/:evaluationId",
    element: <EvaluationDetailsPage />,
  },
];
