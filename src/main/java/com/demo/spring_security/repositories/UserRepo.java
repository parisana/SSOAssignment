package com.demo.spring_security.repositories;

import com.demo.spring_security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Parisana
 */
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
}
