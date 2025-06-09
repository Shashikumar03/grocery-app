package org.example.grocery_app.exception;


import jakarta.validation.ConstraintViolationException;
import org.example.grocery_app.apiPayload.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgsNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> resp = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            resp.put(fieldName, message);
        });

        return new ResponseEntity<Map<String, String>>(resp, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse> handleApiException(ApiException ex) {
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String rootCauseMessage = ex.getMostSpecificCause().getMessage();
        String message;

        if (rootCauseMessage.contains("name")) {
            message = "यह name पहले से उपयोग में है, कृपया कोई और नाम चुनें।";
        } else if (rootCauseMessage.contains("email")) {
            message = "यह email पहले से registered है। कृपया कोई और ईमेल आज़माएं।";
        } else if (rootCauseMessage.contains("phone_number") || rootCauseMessage.contains("phoneNumber")) {
            message = "यह फ़ोन नंबर पहले से पंजीकृत है। कृपया कोई और फ़ोन नंबर उपयोग करें।";
        } else {
            message = "डेटाबेस त्रुटि: कृपया मान्य जानकारी प्रदान करें।";
        }

        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        ex.getMessage();
        Map<String, String> errors = new HashMap<>();

        // Iterate through each violation
        ex.getConstraintViolations().forEach(violation -> {
            // Get the property path (e.g., "stockQuantity")
            String propertyPath = violation.getPropertyPath().toString();
            // Get the message (e.g., "Stock quantity cannot be negative")
            String message = violation.getMessage();
            // Store in the errors map
            errors.put(propertyPath, message);
        });
        System.out.println(errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}