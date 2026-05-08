package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricCriterion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RubricCriterionRepository extends JpaRepository<RubricCriterion, Long> {

	List<RubricCriterion> findByRubricIdOrderBySortOrderAsc(Long rubricId);
}
