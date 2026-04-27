package com.blissfuljuan.aiprojecteval.courseclass.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassRequest;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassResponse;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CourseClassServiceImpl implements CourseClassService {

	private final CourseClassRepository courseClassRepository;

	CourseClassServiceImpl(CourseClassRepository courseClassRepository) {
		this.courseClassRepository = courseClassRepository;
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

	private CourseClass findCourseClass(Long id) {
		return courseClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course class not found"));
	}

	private boolean hasCode(String code) {
		return code != null && !code.isBlank();
	}
}
