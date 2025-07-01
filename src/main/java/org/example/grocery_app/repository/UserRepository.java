package org.example.grocery_app.repository;

import org.example.grocery_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    Optional<List<User>> findByRole(String role);

    Optional<User> findByPhoneNumber(String mobileNumber);
}
