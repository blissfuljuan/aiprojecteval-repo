package com.blissfuljuan.aiprojecteval.identity.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blissfuljuan.aiprojecteval.TestDataFactory;
import com.blissfuljuan.aiprojecteval.common.exception.GlobalExceptionHandler;
import com.blissfuljuan.aiprojecteval.identity.dto.AuthResponse;
import com.blissfuljuan.aiprojecteval.identity.dto.LoginRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.RegisterRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.service.AuthService;
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
@Import({AuthControllerTest.TestConfig.class, GlobalExceptionHandler.class})
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthService authService;

	@BeforeEach
	void resetMocks() {
		org.mockito.Mockito.reset(authService);
	}

	@Test
	void shouldRegisterSuccessfully() throws Exception {
		AuthResponse response = TestDataFactory.createAuthResponse(Role.ADMIN);
		when(authService.register(new RegisterRequest(
				"Admin", null, "User", "admin@example.com", "password123", Role.ADMIN)))
				.thenReturn(response);

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "firstName": "Admin",
								  "middleName": null,
								  "lastName": "User",
								  "email": "admin@example.com",
								  "password": "password123",
								  "role": "ADMIN"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.token").value("jwt-token"))
				.andExpect(jsonPath("$.data.user.email").value("admin@example.com"));
	}

	@Test
	void shouldLoginSuccessfully() throws Exception {
		AuthResponse response = TestDataFactory.createAuthResponse(Role.ADMIN);
		when(authService.login(new LoginRequest("admin@example.com", "password123")))
				.thenReturn(response);

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "admin@example.com",
								  "password": "password123"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.token").value("jwt-token"));
	}

	@Test
	void shouldReturnValidationErrorForInvalidRegisterRequest() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "fullName": "",
								  "email": "invalid-email",
								  "password": "123",
								  "role": null
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Validation failed")));
	}

	@Test
	void shouldReturnValidationErrorForInvalidLoginRequest() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "",
								  "password": ""
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Validation failed")));
	}

	@Test
	void shouldReturnCurrentUserWhenAuthenticated() throws Exception {
		UserResponse response = TestDataFactory.createUserResponse(Role.ADMIN);
		when(authService.getCurrentUser("admin@example.com")).thenReturn(response);

		mockMvc.perform(get("/api/auth/me").with(user("admin@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.email").value("admin@example.com"))
				.andExpect(jsonPath("$.data.role").value("ADMIN"));
	}

	@Test
	void shouldLogoutWhenAuthenticated() throws Exception {
		mockMvc.perform(post("/api/auth/logout").with(user("admin@example.com")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Logged out successfully"));

		verify(authService).logout();
	}

	@Test
	void shouldReturnUnauthorizedForMeWithoutToken() throws Exception {
		mockMvc.perform(get("/api/auth/me"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldReturnUnauthorizedForLogoutWithoutToken() throws Exception {
		mockMvc.perform(post("/api/auth/logout"))
				.andExpect(status().isUnauthorized());
	}

	static class TestConfig {

		@Bean
		@Primary
		AuthService authService() {
			return mock(AuthService.class);
		}
	}
}
