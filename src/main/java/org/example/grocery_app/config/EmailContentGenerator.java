package org.example.grocery_app.config;

//package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.entities.Order;
import org.example.grocery_app.entities.User;
import org.springframework.stereotype.Component;

@Component
public class EmailContentGenerator {

    public String generateOrderConfirmationEmail(User user, Order order) {
        return """
                    <h2 style="color: #2e6c80;">🛒 Order Confirmation - Bazzario</h2>
                    <p>Hello <strong>%s</strong>,</p>
                    <p>Thank you for your order! Here are the details:</p>
                
                    <table style="border: 1px solid #ccc; border-collapse: collapse;">
                        <tr><td><strong>Order ID:</strong></td><td>%s</td></tr>
                        <tr><td><strong>User Mob:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Payment Mode:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Total Amount:</strong></td><td>₹%.2f</td></tr>
                        <tr><td><strong>Delivery City:</strong></td><td>%s</td></tr>
                        <tr><td><strong>Address:</strong></td><td>%s</td></tr>
                    </table>
                
                    <p style="margin-top: 16px;">📦 Your order will be delivered soon. We’ll notify you when it’s out for delivery.</p>
                
                    <p>Thanks for shopping with Bazzario!<br/>— Team Bazzario</p>
                """.formatted(
                user.getName(),
                order.getId(),
                user.getPhoneNumber(),
                order.getPaymentMode(),
                order.getCart().getTotalPricesOfAllProduct(),
                order.getCity(),
                order.getAddress()
        );
    }

    public String[] getOrderConfirmationRecipients() {
        return new String[] {
                "shashikumarkushwaha1@gmail.com",
                "ry4715885@gmail.com"
                // Add user.getEmail() if needed, currently commented out in original code
        };
    }
}