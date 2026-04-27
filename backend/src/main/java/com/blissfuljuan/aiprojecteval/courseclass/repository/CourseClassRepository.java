package com.blissfuljuan.aiprojecteval.courseclass.repository;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseClassRepository extends JpaRepository<CourseClass, Long> {

	boolean existsByCode(String code);

	boolean existsByCodeAndIdNot(String code, Long id);

	List<CourseClass> findAllByOrderByNameAsc();
}
