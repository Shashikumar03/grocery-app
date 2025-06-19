package org.example.grocery_app.controller;

import org.example.grocery_app.component.PdfGenerator;
import org.example.grocery_app.config.FirebaseStorageService;
import org.example.grocery_app.dto.HisabBookDto;
import org.example.grocery_app.dto.OrderDetailsToAdminDto;
import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.dto.PriceSettlementDto;
import org.example.grocery_app.security.JwtHelper;
//import org.example.grocery_app.service.FirebaseStorageService;
import org.example.grocery_app.service.OrderService;
import org.example.grocery_app.service.PriceSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    @Autowired
    private  OrderService orderService;
    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private PriceSettlementService priceSettlementService;

    @Autowired
    private PdfGenerator pdfGenerator;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @PostMapping("/{paymentId}/confirm-return")
    public ResponseEntity<OrderDto> confirmGoodsReturnedAndInitiateRefund(@PathVariable Long paymentId) {
        OrderDto orderDto = orderService.confirmGoodsReturnedAndRefund(paymentId);
        return ResponseEntity.ok(orderDto);
    }


    @GetMapping("/recent-orders")
    public ResponseEntity<List<OrderDetailsToAdminDto>> getRecentOrders() {
        List<OrderDetailsToAdminDto> ordersPlacedWithinLastMinute = orderService.getOrdersPlacedWithinLastMinute();
        return ResponseEntity.ok(ordersPlacedWithinLastMinute);
    }
    @GetMapping("/istokenexpire")
    public boolean isTokenExpire(@RequestHeader("Authorization") String authorizationHeader) {
        // The token usually comes as "Bearer <token>", so you need to extract the token part
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            // handle missing or malformed token as needed
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        return jwtHelper.isTokenExpired(token);
    }

    @PostMapping("/amount-settlement-billing")
    public ResponseEntity<List<HisabBookDto>> priceSettlement(){
        List<HisabBookDto> hisabBookDtos = this.priceSettlementService.doSettlement();
        return  new ResponseEntity<>(hisabBookDtos, HttpStatus.CREATED);
    }



    @GetMapping("/pdf/{date}")
    public ResponseEntity<String> generatePdfReport(@PathVariable String date) {
        try {
            LocalDate reportDate = LocalDate.parse(date); // parse input date string
            List<HisabBookDto> settlements = priceSettlementService.getSettlementsByDate(reportDate);
            String outputPath = "PriceSettlement_" + date + ".pdf";

            // Generate PDF locally
            pdfGenerator.generatePriceSettlementReport(reportDate, settlements, outputPath);

            // Upload to Firebase Storage
            String bucketName = "grocery-app-6fe52.appspot.com"; // your bucket name
            String firebasePath = "reports/" + outputPath;

            firebaseStorageService.uploadFile(outputPath, firebasePath, bucketName);

            return ResponseEntity.ok("PDF generated and uploaded: " + firebasePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @PostMapping("/mark-paid-to-shop-owner")
    public ResponseEntity<String> markAsPaidToShopkeeper(
            @RequestBody List<Long> settlementIds
    ) {
        priceSettlementService.updateSettlementStatusByShopOwner(settlementIds, true);
        return ResponseEntity.ok("Selected settlements marked as paid to shopkeeper.");
    }

}
