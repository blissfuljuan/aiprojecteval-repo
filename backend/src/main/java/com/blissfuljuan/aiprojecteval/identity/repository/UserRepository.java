package com.blissfuljuan.aiprojecteval.identity.repository;

import com.blissfuljuan.aiprojecteval.identity.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
