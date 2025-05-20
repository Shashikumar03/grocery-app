package org.example.grocery_app.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"promoUsages", "offer"})
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promoCodeId;

    @Column(unique = true)
    private String code;
    private int usageLimit;
    private int usageCount;
    private LocalDateTime expiryDate;

    // Other fields (expiration date, discount, etc.)

    @OneToMany(mappedBy = "promoCode")
    private List<PromoUsage> promoUsages;



    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer;


}