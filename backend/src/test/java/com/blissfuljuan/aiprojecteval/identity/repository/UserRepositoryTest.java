package com.blissfuljuan.aiprojecteval.identity.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void shouldFindUserByEmail() {
		User user = createUser("admin@example.com");
		userRepository.saveAndFlush(user);

		assertThat(userRepository.findByEmail("admin@example.com"))
				.isPresent()
				.get()
				.extracting(User::getEmail)
				.isEqualTo("admin@example.com");
	}

	@Test
	void shouldReturnTrueWhenEmailExists() {
		userRepository.saveAndFlush(createUser("admin@example.com"));

		assertThat(userRepository.existsByEmail("admin@example.com")).isTrue();
	}

	@Test
	void shouldReturnFalseWhenEmailDoesNotExist() {
		assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
	}

	@Test
	void shouldEnforceUniqueEmail() {
		userRepository.saveAndFlush(createUser("admin@example.com"));

		assertThatThrownBy(() -> userRepository.saveAndFlush(createUser("admin@example.com")))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	private static User createUser(String email) {
		return new User("Admin", null, "User", email, "encoded-password", Role.ADMIN);
	}
}
