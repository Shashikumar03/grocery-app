package org.example.grocery_app.repository;

import org.example.grocery_app.entities.PromoCode;
import org.example.grocery_app.entities.PromoUsage;
import org.example.grocery_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PromoUsageRepository  extends JpaRepository<PromoUsage, Long> {

    boolean existsByUserAndPromoCode(User user, PromoCode promoCode);

}
