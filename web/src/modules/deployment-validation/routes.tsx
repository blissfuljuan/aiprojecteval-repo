import type { RouteObject } from "react-router";
import { DeploymentValidationPage } from "@/modules/deployment-validation/pages/DeploymentValidationPage";

export const deploymentValidationRoutes: RouteObject[] = [
  {
    path: "/deployment-validation",
    element: <DeploymentValidationPage />,
  },
];
