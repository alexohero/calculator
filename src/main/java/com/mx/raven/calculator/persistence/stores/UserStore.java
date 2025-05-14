package com.mx.raven.calculator.persistence.stores;

import com.mx.raven.calculator.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStore extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
