package org.example.grocery_app.payload;




import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.example.grocery_app.entities.User;

@Data
@Builder
@ToString
public class JwtResponse {
    private String jwtToken;
//    private  String  username;
//    private  String role;
//    private  Long id;
    private User user;
}
