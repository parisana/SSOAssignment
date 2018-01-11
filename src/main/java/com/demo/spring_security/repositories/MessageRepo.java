package com.demo.spring_security.repositories;

import com.demo.spring_security.domain.Message;
import com.demo.spring_security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.List;
import java.util.Optional;

/**
 * @author Parisana
 */
public interface MessageRepo extends JpaRepository<Message, Long>{
//    @PostAuthorize("hasPermission(returnObject, 'read')")
    List<Message> findByToId(Long id);
    // check MessagePermissionEvaluator for more details
//    @PostAuthorize("hasPermission(returnObject, 'read')")
    @PostAuthorize("hasRole('USER')")
    Optional<Message> findById(Long id);
}
