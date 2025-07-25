package org.example.grocery_app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fee_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FeeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feeTableId;

    private int deliveryChargesOnCashOnDelivery = 10;

    private int deliveryChargesOnOnlineDelivery=0;
}
