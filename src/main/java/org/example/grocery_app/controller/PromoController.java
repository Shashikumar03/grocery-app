package org.example.grocery_app.controller;

import org.example.grocery_app.dto.PromoCodeDto;
import org.example.grocery_app.entities.PromoCode;
import org.example.grocery_app.payload.PromoCodeApplyResponse;
import org.example.grocery_app.payload.PromoCodeResponse;
import org.example.grocery_app.service.PromoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/promos")
public class PromoController {

    @Autowired
    private PromoService promoService;

    // Create new promo
    @PostMapping("/")
    public ResponseEntity<PromoCodeDto> createPromo(@RequestBody PromoCodeDto promoCodeDto) {
        return ResponseEntity.ok(promoService.createNewPromoCode(promoCodeDto));
    }

    // Get all promos
    @GetMapping("/all")
    public ResponseEntity<List<PromoCodeDto>> getAllPromos() {
        return ResponseEntity.ok(promoService.getAllPromoCode());
    }

    // Get promo by ID
    @GetMapping("/{promoId}")
    public ResponseEntity<PromoCode> getPromoById(@PathVariable Long promoId) {
        PromoCode promoCodeById = promoService.getPromoCodeById(promoId);
        return new ResponseEntity<>(promoCodeById, HttpStatus.OK);
    }

    // Update promo
    @PutMapping("/{promoId}")
    public ResponseEntity<PromoCodeDto> updatePromo(@PathVariable Long promoId, @RequestBody PromoCodeDto promoCodeDto) {
        return ResponseEntity.ok(promoService.updatePromoCode(promoId, promoCodeDto));
    }

    // Delete promo
    @DeleteMapping("/{promoId}")
    public ResponseEntity<Void> deletePromo(@PathVariable Long promoId) {
        promoService.deletePromoCode(promoId);
        return ResponseEntity.noContent().build();
    }

    // Check if user can apply promo
    @GetMapping("/check")
    public ResponseEntity<Boolean> canApplyPromo(@RequestParam Long userId, @RequestParam String code) {
        return ResponseEntity.ok(promoService.canUserApplyPromoCode(userId, code));
    }

    // Mark promo as used by user
    @PostMapping("/use")
    public ResponseEntity<Void> markUsed(@RequestParam Long userId, @RequestParam String code) {
        promoService.markPromoCodeUsed(userId, code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/apply/{userId}")
    public  ResponseEntity<PromoCodeApplyResponse> applyPromoCode(@RequestParam String promoCode, @PathVariable Long userId){
        PromoCodeApplyResponse promoCodeApplyResponse = this.promoService.applyPromoCode(userId, promoCode);
        return new ResponseEntity<>(promoCodeApplyResponse, HttpStatus.OK);
    }
}