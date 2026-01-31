package org.example.grocery_app.repository;

import org.example.grocery_app.entities.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<UserLocation, Long> {
}
