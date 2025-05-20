package org.example.grocery_app.controller;

import org.example.grocery_app.dto.OfferDto;
import org.example.grocery_app.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offer")
public class OfferController {


    @Autowired
    private OfferService offerService;

    @PostMapping("/create")
    public ResponseEntity<OfferDto> createNewOffer(@RequestBody OfferDto offerDto){
        OfferDto newOffer = this.offerService.createNewOffer(offerDto);
        return  new ResponseEntity<>(newOffer, HttpStatus.CREATED);
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<OfferDto> getOfferById(@PathVariable Long offerId){
        OfferDto offerById = this.offerService.getOfferById(offerId);
        return  new ResponseEntity<>(offerById, HttpStatus.OK);
    }
    @GetMapping("/all")
    public ResponseEntity<List<OfferDto>> getAllOffers(){
        List<OfferDto> allOffers = this.offerService.getAllOffers();
        return new ResponseEntity<>(allOffers, HttpStatus.OK);

    }
}
