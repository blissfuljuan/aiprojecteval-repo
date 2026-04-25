package com.blissfuljuan.aiprojecteval.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAllowSwaggerWithoutAuthentication() throws Exception {
		int status = mockMvc.perform(get("/v3/api-docs"))
				.andReturn()
				.getResponse()
				.getStatus();

		assertThat(status).isNotEqualTo(401);
	}

	@Test
	void shouldAllowHealthWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/health"))
				.andExpect(status().isOk());
	}

	@Test
	void shouldRejectProtectedEndpointWithoutToken() throws Exception {
		mockMvc.perform(get("/api/auth/me"))
				.andExpect(status().isUnauthorized());
	}
}
