package org.example.grocery_app.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PriceSettlementDto {

//    private List<OrderDto> orderDto;
    private  HisabBookDto hisabBookDto;
//    private  String orderName;

    private LocalDateTime settlementDate;
    private Long shopkeeperAmount;

    private Long DeveloperAmount;
    private  LocalDateTime dateOfBilling;
}
