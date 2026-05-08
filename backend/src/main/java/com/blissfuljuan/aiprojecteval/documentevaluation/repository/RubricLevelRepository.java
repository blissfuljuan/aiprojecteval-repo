package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.RubricLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RubricLevelRepository extends JpaRepository<RubricLevel, Long> {

	List<RubricLevel> findByCriterionIdOrderBySortOrderAsc(Long criterionId);
}
