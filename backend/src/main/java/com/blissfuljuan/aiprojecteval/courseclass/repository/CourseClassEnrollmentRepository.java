package com.blissfuljuan.aiprojecteval.courseclass.repository;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClassEnrollment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseClassEnrollmentRepository extends JpaRepository<CourseClassEnrollment, Long> {

	boolean existsByStudentIdAndCourseClassId(Long studentId, Long courseClassId);

	List<CourseClassEnrollment> findByStudentIdOrderByCourseClass_NameAsc(Long studentId);

	Optional<CourseClassEnrollment> findByStudentIdAndCourseClassId(Long studentId, Long courseClassId);
}
