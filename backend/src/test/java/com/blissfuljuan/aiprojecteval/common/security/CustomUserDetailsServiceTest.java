package com.blissfuljuan.aiprojecteval.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.TestDataFactory;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@Test
	void shouldLoadUserByEmail() {
		User user = TestDataFactory.createUser(Role.ADMIN);
		when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
		CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

		UserDetails userDetails = service.loadUserByUsername("admin@example.com");

		assertThat(userDetails.getUsername()).isEqualTo("admin@example.com");
		assertThat(userDetails.getPassword()).isEqualTo("encoded-password");
	}

	@Test
	void shouldThrowUsernameNotFoundExceptionWhenEmailDoesNotExist() {
		when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
		CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

		assertThatThrownBy(() -> service.loadUserByUsername("missing@example.com"))
				.isInstanceOf(UsernameNotFoundException.class);
	}

	@Test
	void shouldMapRoleToGrantedAuthority() {
		User user = TestDataFactory.createUser(Role.INSTRUCTOR);
		when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
		CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

		UserDetails userDetails = service.loadUserByUsername("admin@example.com");

		assertThat(userDetails.getAuthorities())
				.extracting("authority")
				.containsExactly("ROLE_INSTRUCTOR");
	}
}
