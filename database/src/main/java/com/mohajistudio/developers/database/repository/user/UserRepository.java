package com.mohajistudio.developers.database.repository.user;

import com.mohajistudio.developers.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, UserCustomRepository {
    Optional<User> findByEmail(String email);
}