import type { RouteObject } from "react-router";
import { RequireRole } from "@/common/guards/RequireRole";
import {
  documentEvaluationAdminRoles,
  documentEvaluationEvaluatorRoles,
  documentEvaluationInstructorRoles,
  documentEvaluationPaths,
  documentEvaluationOverviewRoles,
  documentEvaluationSubmissionRoles,
} from "@/modules/document-evaluation/constants";
import { DocumentCompletenessPage } from "@/modules/document-evaluation/pages/DocumentCompletenessPage";
import { DocumentEvaluationHomePage } from "@/modules/document-evaluation/pages/DocumentEvaluationHomePage";
import { DocumentRequirementPresetDetailsPage } from "@/modules/document-evaluation/pages/DocumentRequirementPresetDetailsPage";
import { DocumentRequirementPresetFormPage } from "@/modules/document-evaluation/pages/DocumentRequirementPresetFormPage";
import { DocumentRequirementPresetsPage } from "@/modules/document-evaluation/pages/DocumentRequirementPresetsPage";
import { DocumentRequirementSetAssignmentsPage } from "@/modules/document-evaluation/pages/DocumentRequirementSetAssignmentsPage";
import { DocumentRequirementSetDetailsPage } from "@/modules/document-evaluation/pages/DocumentRequirementSetDetailsPage";
import { DocumentRequirementSetFormPage } from "@/modules/document-evaluation/pages/DocumentRequirementSetFormPage";
import { DocumentRequirementSetsPage } from "@/modules/document-evaluation/pages/DocumentRequirementSetsPage";
import { DocumentSubmissionsPage } from "@/modules/document-evaluation/pages/DocumentSubmissionsPage";
import { ManualDocumentEvaluationDetailsPage } from "@/modules/document-evaluation/pages/ManualDocumentEvaluationDetailsPage";
import { ManualDocumentEvaluationsPage } from "@/modules/document-evaluation/pages/ManualDocumentEvaluationsPage";
import { MyDocumentEvaluationResultDetailsPage } from "@/modules/document-evaluation/pages/MyDocumentEvaluationResultDetailsPage";
import { MyDocumentEvaluationResultsPage } from "@/modules/document-evaluation/pages/MyDocumentEvaluationResultsPage";

export const documentEvaluationRoutes: RouteObject[] = [
  {
    element: <RequireRole allowedRoles={documentEvaluationOverviewRoles} />,
    children: [
      {
        path: documentEvaluationPaths.home,
        element: <DocumentEvaluationHomePage />,
      },
    ],
  },
  {
    element: <RequireRole allowedRoles={documentEvaluationSubmissionRoles} />,
    children: [
      {
        path: documentEvaluationPaths.submissions,
        element: <DocumentSubmissionsPage />,
      },
    ],
  },
  {
    element: <RequireRole allowedRoles={documentEvaluationAdminRoles} />,
    children: [
      {
        path: documentEvaluationPaths.presetCreate,
        element: <DocumentRequirementPresetFormPage />,
      },
      {
        path: "/document-evaluation/presets/:presetId/edit",
        element: <DocumentRequirementPresetFormPage />,
      },
    ],
  },
  {
    element: <RequireRole allowedRoles={documentEvaluationInstructorRoles} />,
    children: [
      {
        path: documentEvaluationPaths.presets,
        element: <DocumentRequirementPresetsPage />,
      },
      {
        path: "/document-evaluation/presets/:presetId",
        element: <DocumentRequirementPresetDetailsPage />,
      },
      {
        path: documentEvaluationPaths.requirementSets,
        element: <DocumentRequirementSetsPage />,
      },
      {
        path: documentEvaluationPaths.requirementSetCreate,
        element: <DocumentRequirementSetFormPage />,
      },
      {
        path: "/document-evaluation/requirement-sets/:requirementSetId/edit",
        element: <DocumentRequirementSetFormPage />,
      },
      {
        path: "/document-evaluation/requirement-sets/:requirementSetId",
        element: <DocumentRequirementSetDetailsPage />,
      },
      {
        path: documentEvaluationPaths.assignments,
        element: <DocumentRequirementSetAssignmentsPage />,
      },
      {
        path: documentEvaluationPaths.completeness,
        element: <DocumentCompletenessPage />,
      },
    ],
  },
  {
    element: <RequireRole allowedRoles={documentEvaluationEvaluatorRoles} />,
    children: [
      {
        path: documentEvaluationPaths.evaluations,
        element: <ManualDocumentEvaluationsPage />,
      },
      {
        path: "/document-evaluation/evaluations/:evaluationId",
        element: <ManualDocumentEvaluationDetailsPage />,
      },
    ],
  },
  {
    element: <RequireRole allowedRoles={["STUDENT"]} />,
    children: [
      {
        path: documentEvaluationPaths.myResults,
        element: <MyDocumentEvaluationResultsPage />,
      },
      {
        path: "/document-evaluation/my-results/:resultId",
        element: <MyDocumentEvaluationResultDetailsPage />,
      },
    ],
  },
];
