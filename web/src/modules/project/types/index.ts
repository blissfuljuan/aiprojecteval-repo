export type Project = {
  id: number;
  ownerUserId: number;
  ownerEmail: string;
  title: string;
  description: string | null;
  repositoryUrl: string | null;
  projectProposalId: number | null;
  createdAt: string;
  updatedAt: string;
};

export type ProjectRequest = {
  title: string;
  description?: string;
  repositoryUrl?: string;
};
