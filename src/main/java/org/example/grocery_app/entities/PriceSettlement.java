package org.example.grocery_app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class PriceSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceSettlementId;


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


    public double getPayToShopKeeper() {
        return shopkeeperPrice * productQuantity;
    }
    public  double getProfit(){
        return  totalPrice-getPayToShopKeeper();
    }


}
