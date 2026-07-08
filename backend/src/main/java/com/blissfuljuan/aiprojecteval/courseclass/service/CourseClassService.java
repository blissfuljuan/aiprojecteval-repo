package com.blissfuljuan.aiprojecteval.courseclass.service;

import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassEnrollmentRequest;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassRequest;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassResponse;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import java.util.List;

public interface CourseClassService {

	CourseClassResponse create(CourseClassRequest request);

	List<CourseClassResponse> findAll();

	CourseClassResponse findById(Long id);

	CourseClassResponse update(Long id, CourseClassRequest request);

	void delete(Long id);

	CourseClassResponse enroll(String currentUserEmail, CourseClassEnrollmentRequest request);

	List<CourseClassResponse> findMyCourseClasses(String currentUserEmail);

	void unenroll(String currentUserEmail, Long courseClassId);

	CourseClass getCourseClassEntity(Long id);

	boolean isStudentEnrolled(Long studentId, Long courseClassId);
}
