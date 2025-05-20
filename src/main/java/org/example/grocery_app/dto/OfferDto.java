package org.example.grocery_app.dto;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class OfferDto {

    private Long offerId;

    private String name;
    private String description;
    private BigDecimal discountAmount;
}
