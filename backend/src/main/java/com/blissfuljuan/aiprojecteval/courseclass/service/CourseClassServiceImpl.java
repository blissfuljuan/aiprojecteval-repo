package com.blissfuljuan.aiprojecteval.courseclass.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassEnrollmentRequest;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassRequest;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassResponse;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClassEnrollment;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassEnrollmentRepository;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CourseClassServiceImpl implements CourseClassService {

	private final CourseClassRepository courseClassRepository;
	private final CourseClassEnrollmentRepository courseClassEnrollmentRepository;
	private final UserRepository userRepository;

	CourseClassServiceImpl(
			CourseClassRepository courseClassRepository,
			CourseClassEnrollmentRepository courseClassEnrollmentRepository,
			UserRepository userRepository) {
		this.courseClassRepository = courseClassRepository;
		this.courseClassEnrollmentRepository = courseClassEnrollmentRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public CourseClassResponse create(CourseClassRequest request) {
		if (hasCode(request.code()) && courseClassRepository.existsByCode(request.code())) {
			throw new BadRequestException("A course class with code '" + request.code() + "' already exists");
		}

		CourseClass courseClass = new CourseClass(request.name(), request.code());
		return CourseClassResponse.fromEntity(courseClassRepository.save(courseClass));
	}

	@Override
	@Transactional(readOnly = true)
	public List<CourseClassResponse> findAll() {
		return courseClassRepository.findAllByOrderByNameAsc()
				.stream()
				.map(CourseClassResponse::fromEntity)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CourseClassResponse findById(Long id) {
		return CourseClassResponse.fromEntity(findCourseClass(id));
	}

	@Override
	@Transactional
	public CourseClassResponse update(Long id, CourseClassRequest request) {
		CourseClass courseClass = findCourseClass(id);

		if (hasCode(request.code()) && courseClassRepository.existsByCodeAndIdNot(request.code(), id)) {
			throw new BadRequestException("A course class with code '" + request.code() + "' already exists");
		}

		courseClass.setName(request.name());
		courseClass.setCode(request.code());
		return CourseClassResponse.fromEntity(courseClassRepository.save(courseClass));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		CourseClass courseClass = findCourseClass(id);
		courseClassRepository.delete(courseClass);
	}

	@Override
	@Transactional
	public CourseClassResponse enroll(String currentUserEmail, CourseClassEnrollmentRequest request) {
		User student = findStudentByEmail(currentUserEmail);
		if (request.code() == null || request.code().isBlank()) {
			throw new BadRequestException("Class code is required");
		}

		String code = request.code().trim();
		CourseClass courseClass = courseClassRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new BadRequestException("Invalid class code"));

		if (courseClassEnrollmentRepository.existsByStudentIdAndCourseClassId(student.getId(), courseClass.getId())) {
			throw new BadRequestException("You are already enrolled in this course class");
		}

		courseClassEnrollmentRepository.save(new CourseClassEnrollment(student, courseClass));
		return CourseClassResponse.fromEntity(courseClass);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CourseClassResponse> findMyCourseClasses(String currentUserEmail) {
		User student = findStudentByEmail(currentUserEmail);

		return courseClassEnrollmentRepository.findByStudentIdOrderByCourseClass_NameAsc(student.getId())
				.stream()
				.map(CourseClassEnrollment::getCourseClass)
				.map(CourseClassResponse::fromEntity)
				.toList();
	}

	@Override
	@Transactional
	public void unenroll(String currentUserEmail, Long courseClassId) {
		User student = findStudentByEmail(currentUserEmail);
		CourseClassEnrollment enrollment = courseClassEnrollmentRepository
				.findByStudentIdAndCourseClassId(student.getId(), courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException("Course class enrollment not found"));

		courseClassEnrollmentRepository.delete(enrollment);
	}

	private CourseClass findCourseClass(Long id) {
		return courseClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course class not found"));
	}

	private User findStudentByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (user.getRole() != Role.STUDENT) {
			throw new BadRequestException("Only students can manage course class enrollments");
		}

		return user;
	}

	private boolean hasCode(String code) {
		return code != null && !code.isBlank();
	}
}
