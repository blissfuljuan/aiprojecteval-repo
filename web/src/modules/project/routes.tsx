import type { RouteObject } from "react-router";
import { ProjectCreatePage } from "@/modules/project/pages/ProjectCreatePage";
import { ProjectDetailsPage } from "@/modules/project/pages/ProjectDetailsPage";
import { ProjectListPage } from "@/modules/project/pages/ProjectListPage";

export const projectRoutes: RouteObject[] = [
  {
    path: "/projects",
    element: <ProjectListPage />,
  },
  {
    path: "/projects/create",
    element: <ProjectCreatePage />,
  },
  {
    path: "/projects/:projectId",
    element: <ProjectDetailsPage />,
  },
];
