package com.blissfuljuan.aiprojecteval.courseclass.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseClassServiceImplTest {

	@Mock
	private CourseClassRepository courseClassRepository;

	@Mock
	private CourseClassEnrollmentRepository courseClassEnrollmentRepository;

	@Mock
	private UserRepository userRepository;

	private CourseClassServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new CourseClassServiceImpl(courseClassRepository, courseClassEnrollmentRepository, userRepository);
	}

	@Test
	void shouldCreateCourseClass() {
		CourseClassRequest request = new CourseClassRequest("Software Engineering", "SE201");
		CourseClass saved = courseClassWithId(1L, "Software Engineering", "SE201");
		when(courseClassRepository.existsByCode("SE201")).thenReturn(false);
		when(courseClassRepository.save(any(CourseClass.class))).thenReturn(saved);

		CourseClassResponse response = service.create(request);

		assertThat(response.name()).isEqualTo("Software Engineering");
		assertThat(response.code()).isEqualTo("SE201");
	}

	@Test
	void shouldCreateCourseClassWithoutCode() {
		CourseClassRequest request = new CourseClassRequest("Software Engineering", null);
		CourseClass saved = courseClassWithId(1L, "Software Engineering", null);
		when(courseClassRepository.save(any(CourseClass.class))).thenReturn(saved);

		CourseClassResponse response = service.create(request);

		assertThat(response.name()).isEqualTo("Software Engineering");
		assertThat(response.code()).isNull();
	}

	@Test
	void shouldRejectDuplicateCode() {
		CourseClassRequest request = new CourseClassRequest("Software Engineering", "SE201");
		when(courseClassRepository.existsByCode("SE201")).thenReturn(true);

		assertThatThrownBy(() -> service.create(request))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("SE201");
	}

	@Test
	void shouldReturnAllCourseClassesOrderedByName() {
		List<CourseClass> classes = List.of(
				courseClassWithId(1L, "Capstone", "CAP101"),
				courseClassWithId(2L, "Software Engineering", "SE201")
		);
		when(courseClassRepository.findAllByOrderByNameAsc()).thenReturn(classes);

		List<CourseClassResponse> result = service.findAll();

		assertThat(result).hasSize(2);
		assertThat(result.get(0).name()).isEqualTo("Capstone");
		assertThat(result.get(1).name()).isEqualTo("Software Engineering");
	}

	@Test
	void shouldFindById() {
		CourseClass courseClass = courseClassWithId(1L, "Capstone", "CAP101");
		when(courseClassRepository.findById(1L)).thenReturn(Optional.of(courseClass));

		CourseClassResponse response = service.findById(1L);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.name()).isEqualTo("Capstone");
	}

	@Test
	void shouldThrowWhenNotFound() {
		when(courseClassRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.findById(99L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldUpdateCourseClass() {
		CourseClass existing = courseClassWithId(1L, "Old Name", "OLD01");
		when(courseClassRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(courseClassRepository.existsByCodeAndIdNot("NEW01", 1L)).thenReturn(false);
		when(courseClassRepository.save(any(CourseClass.class))).thenReturn(existing);

		CourseClassResponse response = service.update(1L, new CourseClassRequest("New Name", "NEW01"));

		assertThat(response.name()).isEqualTo("New Name");
		assertThat(response.code()).isEqualTo("NEW01");
	}

	@Test
	void shouldRejectUpdateWithDuplicateCode() {
		CourseClass existing = courseClassWithId(1L, "Old Name", "OLD01");
		when(courseClassRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(courseClassRepository.existsByCodeAndIdNot("SE201", 1L)).thenReturn(true);

		assertThatThrownBy(() -> service.update(1L, new CourseClassRequest("New Name", "SE201")))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("SE201");
	}

	@Test
	void shouldDeleteCourseClass() {
		CourseClass courseClass = courseClassWithId(1L, "Capstone", "CAP101");
		when(courseClassRepository.findById(1L)).thenReturn(Optional.of(courseClass));

		service.delete(1L);

		verify(courseClassRepository).delete(courseClass);
	}

	@Test
	void shouldEnrollStudentByClassCode() {
		User student = userWithId(7L, Role.STUDENT);
		CourseClass courseClass = courseClassWithId(1L, "Capstone", "CAP101");
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(courseClassRepository.findByCodeIgnoreCase("CAP101")).thenReturn(Optional.of(courseClass));
		when(courseClassEnrollmentRepository.existsByStudentIdAndCourseClassId(7L, 1L)).thenReturn(false);

		CourseClassResponse response = service.enroll("student@example.com", new CourseClassEnrollmentRequest(" CAP101 "));

		assertThat(response.id()).isEqualTo(1L);
		verify(courseClassEnrollmentRepository).save(any(CourseClassEnrollment.class));
	}

	@Test
	void shouldRejectInvalidClassCode() {
		User student = userWithId(7L, Role.STUDENT);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(courseClassRepository.findByCodeIgnoreCase("BADCODE")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.enroll("student@example.com", new CourseClassEnrollmentRequest("BADCODE")))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Invalid class code");
	}

	@Test
	void shouldRejectDuplicateEnrollment() {
		User student = userWithId(7L, Role.STUDENT);
		CourseClass courseClass = courseClassWithId(1L, "Capstone", "CAP101");
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(courseClassRepository.findByCodeIgnoreCase("CAP101")).thenReturn(Optional.of(courseClass));
		when(courseClassEnrollmentRepository.existsByStudentIdAndCourseClassId(7L, 1L)).thenReturn(true);

		assertThatThrownBy(() -> service.enroll("student@example.com", new CourseClassEnrollmentRequest("CAP101")))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("already enrolled");
	}

	@Test
	void shouldReturnStudentEnrolledCourseClasses() {
		User student = userWithId(7L, Role.STUDENT);
		CourseClassEnrollment enrollment = new CourseClassEnrollment(student, courseClassWithId(1L, "Capstone", "CAP101"));
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(courseClassEnrollmentRepository.findByStudentIdOrderByCourseClass_NameAsc(7L))
				.thenReturn(List.of(enrollment));

		List<CourseClassResponse> result = service.findMyCourseClasses("student@example.com");

		assertThat(result).hasSize(1);
		assertThat(result.get(0).name()).isEqualTo("Capstone");
	}

	@Test
	void shouldUnenrollStudentFromCourseClass() {
		User student = userWithId(7L, Role.STUDENT);
		CourseClassEnrollment enrollment = new CourseClassEnrollment(student, courseClassWithId(1L, "Capstone", "CAP101"));
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(courseClassEnrollmentRepository.findByStudentIdAndCourseClassId(7L, 1L))
				.thenReturn(Optional.of(enrollment));

		service.unenroll("student@example.com", 1L);

		verify(courseClassEnrollmentRepository).delete(enrollment);
	}

	@Test
	void shouldRejectEnrollmentForNonStudent() {
		User instructor = userWithId(7L, Role.INSTRUCTOR);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));

		assertThatThrownBy(() -> service.findMyCourseClasses("instructor@example.com"))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Only students");
	}

	private CourseClass courseClassWithId(Long id, String name, String code) {
		CourseClass cc = new CourseClass(name, code);
		cc.setId(id);
		return cc;
	}

	private User userWithId(Long id, Role role) {
		User user = new User("Test", null, "User", "student@example.com", "password", role);
		user.setId(id);
		return user;
	}
}
