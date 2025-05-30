package org.example.grocery_app.repository;

import org.example.grocery_app.dto.PasswordResetTokenDto;
import org.example.grocery_app.entities.PasswordResetToken;
import org.example.grocery_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository  extends JpaRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByUser(User user);

    Optional<PasswordResetToken> findByEmail(String email);

}
