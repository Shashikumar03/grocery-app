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
public class PromoUsageDto {

    private Long promoUsageId;
    private Long userId;
    private Long promoCodeId;
    private boolean used;
    private LocalDateTime usedAt;
}