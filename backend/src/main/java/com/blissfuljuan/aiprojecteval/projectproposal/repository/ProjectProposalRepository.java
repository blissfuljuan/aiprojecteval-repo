package com.blissfuljuan.aiprojecteval.projectproposal.repository;

import com.blissfuljuan.aiprojecteval.projectproposal.model.ProjectProposal;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProposalStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectProposalRepository extends JpaRepository<ProjectProposal, Long> {

	List<ProjectProposal> findBySubmittedById(Long userId);

	List<ProjectProposal> findByCourseClassId(Long courseClassId);

	List<ProjectProposal> findByStatus(ProposalStatus status);

	boolean existsBySubmittedByIdAndStatusIn(Long userId, Collection<ProposalStatus> statuses);
}
