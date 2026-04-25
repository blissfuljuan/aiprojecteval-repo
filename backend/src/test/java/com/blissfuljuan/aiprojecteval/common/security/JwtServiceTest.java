package com.blissfuljuan.aiprojecteval.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.blissfuljuan.aiprojecteval.TestDataFactory;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import io.jsonwebtoken.JwtException;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

	private static final String SECRET = "test-jwt-secret-key-with-at-least-32-characters";
	private static final long EXPIRATION_MS = 86400000L;

	private JwtService jwtService;
	private User user;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService(SECRET, EXPIRATION_MS);
		user = TestDataFactory.createUser(Role.ADMIN);
	}

	@Test
	void shouldGenerateValidToken() {
		String token = jwtService.generateToken(user);

		assertThat(token).isNotBlank();
		assertThat(jwtService.extractUsername(token)).isEqualTo(user.getEmail());
	}

	@Test
	void shouldExtractUsernameFromToken() {
		String token = jwtService.generateToken(user);

		assertThat(jwtService.extractUsername(token)).isEqualTo("admin@example.com");
	}

	@Test
	void shouldExtractExpirationFromToken() {
		String token = jwtService.generateToken(user);

		assertThat(jwtService.extractExpiration(token)).isAfter(new Date());
	}

	@Test
	void shouldValidateTokenForMatchingUser() {
		String token = jwtService.generateToken(user);
		UserDetails userDetails = org.springframework.security.core.userdetails.User
				.withUsername("admin@example.com")
				.password("encoded-password")
				.authorities("ROLE_ADMIN")
				.build();

		assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
	}

	@Test
	void shouldRejectTokenForDifferentUser() {
		String token = jwtService.generateToken(user);
		UserDetails userDetails = org.springframework.security.core.userdetails.User
				.withUsername("other@example.com")
				.password("encoded-password")
				.authorities("ROLE_ADMIN")
				.build();

		assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
	}

	@Test
	void shouldRejectMalformedToken() {
		assertThatThrownBy(() -> jwtService.extractUsername("not-a-valid-token"))
				.isInstanceOf(JwtException.class);
	}
}
