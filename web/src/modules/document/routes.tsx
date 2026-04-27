import type { RouteObject } from "react-router";
import { DocumentAnalysisPage } from "@/modules/document/pages/DocumentAnalysisPage";
import { DocumentDetailsPage } from "@/modules/document/pages/DocumentDetailsPage";
import { DocumentListPage } from "@/modules/document/pages/DocumentListPage";

export const documentRoutes: RouteObject[] = [
  {
    path: "/documents",
    element: <DocumentListPage />,
  },
  {
    path: "/documents/:documentId",
    element: <DocumentDetailsPage />,
  },
  {
    path: "/documents/:documentId/analysis",
    element: <DocumentAnalysisPage />,
  },
];
