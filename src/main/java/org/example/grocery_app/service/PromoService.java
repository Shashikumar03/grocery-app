package org.example.grocery_app.service;

import org.example.grocery_app.dto.PromoCodeDto;
import org.example.grocery_app.entities.PromoCode;
import org.example.grocery_app.payload.PromoCodeApplyResponse;
import org.example.grocery_app.payload.PromoCodeResponse;

import java.util.List;
import java.util.UUID;

public interface PromoService {

    // Create a new promo code
    PromoCodeDto createNewPromoCode(PromoCodeDto promoCodeDto);

    // Get all promo codes
    List<PromoCodeDto> getAllPromoCode();

    // Get a single promo code by ID
    PromoCode getPromoCodeById(Long promoCodeId);

    // Update promo code details (e.g., expiry, limit, discount)
    PromoCodeDto updatePromoCode(Long promoCodeId, PromoCodeDto promoCodeDto);

    // Delete a promo code
    void deletePromoCode(Long promoCodeId);

    // Check if a user can apply a promo code
    boolean canUserApplyPromoCode(Long userId, String promoCode);

    // Mark promo code as used by a user (and increment usage count)
    void markPromoCodeUsed(Long userId, String promoCode);

    // Deactivate expired promo codes (optional cleanup method)
    void deactivateExpiredPromoCodes();


    public PromoCodeApplyResponse applyPromoCode(Long userId,  String code);
}
