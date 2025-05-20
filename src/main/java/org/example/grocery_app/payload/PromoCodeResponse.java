package org.example.grocery_app.payload;


import lombok.*;
import org.example.grocery_app.entities.Offer;
import org.example.grocery_app.entities.PromoCode;
import org.example.grocery_app.entities.PromoUsage;

import java.math.BigDecimal;
import java.util.List;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromoCodeResponse {

    private PromoCode promoCode;
//    private List<PromoUsage> promoUsage;

//    private Offer offer;
}
