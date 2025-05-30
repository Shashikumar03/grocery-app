package org.example.grocery_app.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class ShopkeeperDto {
    private Long id;
    private String shopName;
    private Long userId;
}
