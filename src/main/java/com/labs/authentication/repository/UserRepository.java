package com.labs.authentication.repository;

import com.labs.authentication.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByUsername(String username);
    Optional<Users> findByUsernameOrEmail(String username, String email);
    Optional<Users> findByTokenPassword(String tokenPassword);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
