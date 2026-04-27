package com.blissfuljuan.aiprojecteval.courseclass.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blissfuljuan.aiprojecteval.common.exception.GlobalExceptionHandler;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassRequest;
import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassResponse;
import com.blissfuljuan.aiprojecteval.courseclass.service.CourseClassService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({CourseClassControllerTest.TestConfig.class, GlobalExceptionHandler.class})
class CourseClassControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CourseClassService courseClassService;

	@BeforeEach
	void resetMocks() {
		org.mockito.Mockito.reset(courseClassService);
	}

	@Test
	void shouldRejectUnauthenticatedRequest() throws Exception {
		mockMvc.perform(get("/api/course-classes"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldAllowAnyAuthenticatedUserToListCourseClasses() throws Exception {
		when(courseClassService.findAll()).thenReturn(List.of(sampleResponse()));

		mockMvc.perform(get("/api/course-classes").with(user("student@example.com").roles("STUDENT")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	void shouldAllowInstructorToCreateCourseClass() throws Exception {
		when(courseClassService.create(any(CourseClassRequest.class))).thenReturn(sampleResponse());

		mockMvc.perform(post("/api/course-classes")
						.with(user("instructor@example.com").roles("INSTRUCTOR"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Software Engineering\",\"code\":\"SE201\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name").value("Software Engineering"));
	}

	@Test
	void shouldAllowAdminToCreateCourseClass() throws Exception {
		when(courseClassService.create(any(CourseClassRequest.class))).thenReturn(sampleResponse());

		mockMvc.perform(post("/api/course-classes")
						.with(user("admin@example.com").roles("ADMIN"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Software Engineering\",\"code\":\"SE201\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void shouldRejectStudentFromCreatingCourseClass() throws Exception {
		mockMvc.perform(post("/api/course-classes")
						.with(user("student@example.com").roles("STUDENT"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Software Engineering\",\"code\":\"SE201\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void shouldRejectAdviserFromCreatingCourseClass() throws Exception {
		mockMvc.perform(post("/api/course-classes")
						.with(user("adviser@example.com").roles("ADVISER"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Software Engineering\",\"code\":\"SE201\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void shouldAllowInstructorToUpdateCourseClass() throws Exception {
		when(courseClassService.update(any(Long.class), any(CourseClassRequest.class))).thenReturn(sampleResponse());

		mockMvc.perform(put("/api/course-classes/1")
						.with(user("instructor@example.com").roles("INSTRUCTOR"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Updated Name\",\"code\":\"SE201\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void shouldAllowAdminToDeleteCourseClass() throws Exception {
		mockMvc.perform(delete("/api/course-classes/1")
						.with(user("admin@example.com").roles("ADMIN")))
				.andExpect(status().isOk());
	}

	@Test
	void shouldRejectInstructorFromDeletingCourseClass() throws Exception {
		mockMvc.perform(delete("/api/course-classes/1")
						.with(user("instructor@example.com").roles("INSTRUCTOR")))
				.andExpect(status().isForbidden());
	}

	@Test
	void shouldRejectMissingName() throws Exception {
		mockMvc.perform(post("/api/course-classes")
						.with(user("admin@example.com").roles("ADMIN"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"\",\"code\":\"SE201\"}"))
				.andExpect(status().isBadRequest());
	}

	private CourseClassResponse sampleResponse() {
		return new CourseClassResponse(
				1L,
				"Software Engineering",
				"SE201",
				LocalDateTime.of(2026, 4, 27, 10, 0),
				LocalDateTime.of(2026, 4, 27, 10, 0)
		);
	}

	static class TestConfig {

		@Bean
		@Primary
		public CourseClassService courseClassService() {
			return mock(CourseClassService.class);
		}
	}
}
