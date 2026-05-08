package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRequirementSetAssignmentRepository
		extends JpaRepository<DocumentRequirementSetAssignment, Long> {

	List<DocumentRequirementSetAssignment> findByRequirementSetId(Long requirementSetId);

	List<DocumentRequirementSetAssignment> findByCourseClassId(Long courseClassId);

	List<DocumentRequirementSetAssignment> findByProjectId(Long projectId);

	List<DocumentRequirementSetAssignment> findByCourseClassIdAndStatus(
			Long courseClassId,
			RequirementSetAssignmentStatus status);

	List<DocumentRequirementSetAssignment> findByProjectIdAndStatus(
			Long projectId,
			RequirementSetAssignmentStatus status);

	boolean existsByRequirementSetIdAndCourseClassIdAndStatus(
			Long requirementSetId,
			Long courseClassId,
			RequirementSetAssignmentStatus status);

	boolean existsByRequirementSetIdAndProjectIdAndStatus(
			Long requirementSetId,
			Long projectId,
			RequirementSetAssignmentStatus status);

	List<DocumentRequirementSetAssignment> findByCourseClassIdAndAssignmentTypeAndStatus(
			Long courseClassId,
			RequirementSetAssignmentType assignmentType,
			RequirementSetAssignmentStatus status);

	List<DocumentRequirementSetAssignment> findByProjectIdAndAssignmentTypeAndStatus(
			Long projectId,
			RequirementSetAssignmentType assignmentType,
			RequirementSetAssignmentStatus status);
}
