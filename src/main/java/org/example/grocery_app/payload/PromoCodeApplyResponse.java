package org.example.grocery_app.payload;

import lombok.*;

import java.math.BigDecimal;
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromoCodeApplyResponse {
    private String promoCode;
    private String offerName;
    private String offerDescription;
    private BigDecimal discountAmount;
    private String message;

    // Getters and Setters
}
