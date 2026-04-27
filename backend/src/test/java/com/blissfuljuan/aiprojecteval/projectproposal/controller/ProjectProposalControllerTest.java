package com.blissfuljuan.aiprojecteval.projectproposal.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blissfuljuan.aiprojecteval.common.exception.GlobalExceptionHandler;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.AdviserDecisionRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalResponse;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProposalDecisionRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProposalStatus;
import com.blissfuljuan.aiprojecteval.projectproposal.service.ProjectProposalService;
import java.time.LocalDateTime;
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
@Import({ProjectProposalControllerTest.TestConfig.class, GlobalExceptionHandler.class})
class ProjectProposalControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProjectProposalService projectProposalService;

	@BeforeEach
	void resetMocks() {
		org.mockito.Mockito.reset(projectProposalService);
	}

	@Test
	void shouldRejectUnauthenticatedRequest() throws Exception {
		mockMvc.perform(get("/api/project-proposals"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRejectStudentFromAllProposalsEndpoint() throws Exception {
		mockMvc.perform(get("/api/project-proposals").with(user("student@example.com").roles("STUDENT")))
				.andExpect(status().isForbidden());
	}

	@Test
	void shouldAllowInstructorDecisionEndpointForInstructor() throws Exception {
		ProposalDecisionRequest request = new ProposalDecisionRequest(ProposalStatus.APPROVED, "Approved");
		when(projectProposalService.instructorDecision("instructor@example.com", 10L, request))
				.thenReturn(response(ProposalStatus.APPROVED));

		mockMvc.perform(patch("/api/project-proposals/10/instructor-decision")
						.with(user("instructor@example.com").roles("INSTRUCTOR"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "decision": "APPROVED",
								  "remarks": "Approved"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.status").value("APPROVED"));
	}

	@Test
	void shouldRejectStudentFromInstructorDecisionEndpoint() throws Exception {
		mockMvc.perform(patch("/api/project-proposals/10/instructor-decision")
						.with(user("student@example.com").roles("STUDENT"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "decision": "APPROVED",
								  "remarks": "Approved"
								}
								"""))
				.andExpect(status().isForbidden());
	}

	@Test
	void shouldAllowAdviserToRecordAdviserDecision() throws Exception {
		AdviserDecisionRequest request = new AdviserDecisionRequest(ProposalStatus.ADVISER_REVIEWED, "Scope is feasible");
		when(projectProposalService.adviserDecision("adviser@example.com", 10L, request))
				.thenReturn(response(ProposalStatus.ADVISER_REVIEWED));

		mockMvc.perform(patch("/api/project-proposals/10/adviser-decision")
						.with(user("adviser@example.com").roles("ADVISER"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "decision": "ADVISER_REVIEWED",
								  "remarks": "Scope is feasible"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.status").value("ADVISER_REVIEWED"));
	}

	@Test
	void shouldRejectStudentFromAdviserDecisionEndpoint() throws Exception {
		mockMvc.perform(patch("/api/project-proposals/10/adviser-decision")
						.with(user("student@example.com").roles("STUDENT"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"decision\": \"ADVISER_REVIEWED\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void shouldRejectInstructorFromAdviserDecisionEndpoint() throws Exception {
		mockMvc.perform(patch("/api/project-proposals/10/adviser-decision")
						.with(user("instructor@example.com").roles("INSTRUCTOR"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"decision\": \"ADVISER_REVIEWED\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void shouldAllowAdviserToAccessAllProposals() throws Exception {
		when(projectProposalService.getAllProposals("adviser@example.com"))
				.thenReturn(List.of(response(ProposalStatus.SUBMITTED)));

		mockMvc.perform(get("/api/project-proposals").with(user("adviser@example.com").roles("ADVISER")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].status").value("SUBMITTED"));
	}

	private ProjectProposalResponse response(ProposalStatus status) {
		return new ProjectProposalResponse(
				10L,
				"Capstone Portal",
				"Problem statement",
				"Objectives",
				"Students",
				"Features",
				"Spring Boot",
				"Expected output",
				status,
				1L,
				"Capstone 1",
				null,
				null,
				1L,
				"Student User",
				null,
				"Approved",
				LocalDateTime.now(),
				null,
				null,
				LocalDateTime.now(),
				LocalDateTime.now());
	}

	static class TestConfig {

		@Bean
		@Primary
		ProjectProposalService projectProposalService() {
			return mock(ProjectProposalService.class);
		}
	}
}
