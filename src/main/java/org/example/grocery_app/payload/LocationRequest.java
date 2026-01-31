package org.example.grocery_app.payload;

import lombok.Data;

@Data
public class LocationRequest {
    private Double latitude;
    private Double longitude;
}