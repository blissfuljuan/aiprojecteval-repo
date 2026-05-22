package com.blissfuljuan.aiprojecteval.courseclass.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassEnrollmentRequest;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassRequest;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassResponse;
import com.blissfuljuan.aiprojecteval.courseclass.service.CourseClassService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/course-classes")
public class CourseClassController {

	private final CourseClassService courseClassService;

	public CourseClassController(CourseClassService courseClassService) {
		this.courseClassService = courseClassService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<CourseClassResponse> create(@Valid @RequestBody CourseClassRequest request) {
		return ApiResponse.ok("Course class created", courseClassService.create(request));
	}

	@GetMapping
	public ApiResponse<List<CourseClassResponse>> findAll() {
		return ApiResponse.ok(courseClassService.findAll());
	}

	@PostMapping("/enroll")
	@PreAuthorize("hasRole('STUDENT')")
	public ApiResponse<CourseClassResponse> enroll(
			Authentication authentication,
			@Valid @RequestBody CourseClassEnrollmentRequest request) {
		return ApiResponse.ok("Enrollment completed", courseClassService.enroll(authentication.getName(), request));
	}

	@GetMapping("/my")
	@PreAuthorize("hasRole('STUDENT')")
	public ApiResponse<List<CourseClassResponse>> findMyCourseClasses(Authentication authentication) {
		return ApiResponse.ok(courseClassService.findMyCourseClasses(authentication.getName()));
	}

	@DeleteMapping("/my/{courseClassId}")
	@PreAuthorize("hasRole('STUDENT')")
	public ApiResponse<Void> unenroll(Authentication authentication, @PathVariable Long courseClassId) {
		courseClassService.unenroll(authentication.getName(), courseClassId);
		return ApiResponse.ok("Course class enrollment removed", null);
	}

	@GetMapping("/{id}")
	public ApiResponse<CourseClassResponse> findById(@PathVariable Long id) {
		return ApiResponse.ok(courseClassService.findById(id));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<CourseClassResponse> update(
			@PathVariable Long id,
			@Valid @RequestBody CourseClassRequest request) {
		return ApiResponse.ok("Course class updated", courseClassService.update(id, request));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<Void> delete(@PathVariable Long id) {
		courseClassService.delete(id);
		return ApiResponse.ok("Course class deleted", null);
	}
}
