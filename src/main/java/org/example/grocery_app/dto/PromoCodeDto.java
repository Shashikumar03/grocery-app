package org.example.grocery_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeDto {

    private Long promoCodeId;
    private String code;
    private int usageLimit;
    private int usageCount;
    private LocalDateTime expiryDate;

    private Long offerId;
}