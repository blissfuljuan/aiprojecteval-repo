package com.blissfuljuan.aiprojecteval.courseclass.service;

import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassRequest;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassResponse;
import java.util.List;

public interface CourseClassService {

	CourseClassResponse create(CourseClassRequest request);

	List<CourseClassResponse> findAll();

	CourseClassResponse findById(Long id);

	CourseClassResponse update(Long id, CourseClassRequest request);

	void delete(Long id);
}
