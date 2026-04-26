import type { RouteObject } from "react-router";
import { DocumentUploadPage } from "@/modules/document/pages/DocumentUploadPage";

export const documentRoutes: RouteObject[] = [
  {
    path: "/documents",
    element: <DocumentUploadPage />,
  },
];
