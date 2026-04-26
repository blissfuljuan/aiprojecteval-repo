import type { RouteObject } from "react-router";
import { SubmissionCreatePage } from "@/modules/submission/pages/SubmissionCreatePage";
import { SubmissionDetailsPage } from "@/modules/submission/pages/SubmissionDetailsPage";

export const submissionRoutes: RouteObject[] = [
  {
    path: "/submissions",
    element: <SubmissionCreatePage />,
  },
  {
    path: "/submissions/:submissionId",
    element: <SubmissionDetailsPage />,
  },
];
