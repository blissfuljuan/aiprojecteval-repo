package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.TemplateSection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateSectionRepository extends JpaRepository<TemplateSection, Long> {

	List<TemplateSection> findByTemplateIdOrderBySortOrderAsc(Long templateId);
}
