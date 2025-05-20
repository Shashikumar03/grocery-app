package org.example.grocery_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryAddressDto {

    private Long deliveryAddressId;

    @NotBlank(message = "Address cannot be blank")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;

    @NotBlank(message = "Landmark cannot be blank")
    private String landmark;

    @NotBlank(message = "Mobile number cannot be blank")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobile;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "State cannot be blank")
    private String state;

    @NotBlank(message = "Pin code cannot be blank")
    @Pattern(regexp = "^\\d{6}$", message = "Pin code must be exactly 6 digits")
    private String pin;

//    @NotNull(message = "User ID is required")
    private Long userId; // ✅ new field
}
