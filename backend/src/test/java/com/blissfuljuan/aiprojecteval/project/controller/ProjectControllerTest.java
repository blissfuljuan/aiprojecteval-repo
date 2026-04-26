package com.blissfuljuan.aiprojecteval.project.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blissfuljuan.aiprojecteval.TestDataFactory;
import com.blissfuljuan.aiprojecteval.common.exception.GlobalExceptionHandler;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectRequest;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import com.blissfuljuan.aiprojecteval.project.service.ProjectService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({ProjectControllerTest.TestConfig.class, GlobalExceptionHandler.class})
class ProjectControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProjectService projectService;

	@BeforeEach
	void resetMocks() {
		org.mockito.Mockito.reset(projectService);
	}

	@Test
	void shouldCreateProject() throws Exception {
		ProjectRequest request = TestDataFactory.createProjectRequest();
		ProjectResponse response = TestDataFactory.createProjectResponse();
		when(projectService.create("admin@example.com", request)).thenReturn(response);

		mockMvc.perform(post("/api/projects")
						.with(user("admin@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "Capstone Portal",
								  "description": "AI-assisted project evaluation system",
								  "repositoryUrl": "https://github.com/example/capstone-portal"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Project created"))
				.andExpect(jsonPath("$.data.title").value("Capstone Portal"));
	}

	@Test
	void shouldFindCurrentUsersProjects() throws Exception {
		when(projectService.findAll("admin@example.com"))
				.thenReturn(List.of(TestDataFactory.createProjectResponse()));

		mockMvc.perform(get("/api/projects").with(user("admin@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].ownerEmail").value("admin@example.com"));
	}

	@Test
	void shouldReturnValidationErrorForBlankTitle() throws Exception {
		mockMvc.perform(post("/api/projects")
						.with(user("admin@example.com"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "",
								  "description": "Missing title"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Validation failed")));
	}

	static class TestConfig {

		@Bean
		@Primary
		ProjectService projectService() {
			return mock(ProjectService.class);
		}
	}
}
