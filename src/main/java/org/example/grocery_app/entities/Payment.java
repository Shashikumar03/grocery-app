package org.example.grocery_app.entities;

import com.razorpay.RazorpayClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.razorpay.RazorpayClient;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "order")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    private String paymentMode; // PAYTM, UPI, CARD
    private String paymentStatus; // PENDING, COMPLETED, FAILED
    private LocalDateTime paymentTime;

    private String rozerpayId;
//    this razorpay id is order_id


    private String paymentId;



    private double paymentAmount;

    private double refundAmount;
    private String RefundStatus;

    private  boolean RefundInitiated;

    private  String paymentNotes;
}
