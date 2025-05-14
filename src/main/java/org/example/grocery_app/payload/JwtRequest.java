package org.example.grocery_app.payload;



import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class JwtRequest {
    private String email;
    private  String  password;
}