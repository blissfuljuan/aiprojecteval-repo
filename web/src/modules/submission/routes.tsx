import type { RouteObject } from "react-router";
import { SubmissionListPage } from "@/modules/submission/pages/SubmissionListPage";
import { SubmissionCreatePage } from "@/modules/submission/pages/SubmissionCreatePage";
import { SubmissionDetailsPage } from "@/modules/submission/pages/SubmissionDetailsPage";

export const submissionRoutes: RouteObject[] = [
  {
    path: "/submissions",
    element: <SubmissionListPage />,
  },
  {
    path: "/submissions/create",
    element: <SubmissionCreatePage />,
  },
  {
    path: "/submissions/:submissionId",
    element: <SubmissionDetailsPage />,
  },
];
