package org.example.grocery_app.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExpoPushMessage {
    private String to;
    private String sound = "default";
    private String title;
    private String body;
    private Map<String, Object> data;

    // Getters and setters
}
