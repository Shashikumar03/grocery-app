package org.example.grocery_app.dto;

import lombok.*;
import org.example.grocery_app.entities.ShopProduct;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HisabBookDto {

    private  Long priceSettlementId;
    private  String productName;

    private  Long productId;

    private  double productPrice;

    private double shopkeeperPrice;

    private  Long shopkeeperId;
    private int productQuantity;

    private double totalPrice;


    private LocalDateTime settlementDate;

    private  boolean paidToShopkeeper;

    private boolean paymentDoneByAdmin;


    private double payToShopKeeper;

    private   double getProfit;




}
