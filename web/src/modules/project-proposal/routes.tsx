import type { RouteObject } from "react-router";
import { ProposalCreatePage } from "@/modules/project-proposal/pages/ProposalCreatePage";
import { ProposalDetailsPage } from "@/modules/project-proposal/pages/ProposalDetailsPage";
import { ProposalEditPage } from "@/modules/project-proposal/pages/ProposalEditPage";
import { ProposalListPage } from "@/modules/project-proposal/pages/ProposalListPage";
import { paths } from "@/routes/paths";

export const proposalRoutes: RouteObject[] = [
  {
    path: paths.proposals,
    element: <ProposalListPage />,
  },
  {
    path: paths.proposalCreate,
    element: <ProposalCreatePage />,
  },
  {
    path: paths.proposalDetails(":proposalId"),
    element: <ProposalDetailsPage />,
  },
  {
    path: paths.proposalEdit(":proposalId"),
    element: <ProposalEditPage />,
  },
];
