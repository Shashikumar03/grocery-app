package org.example.grocery_app.service;

import org.example.grocery_app.dto.OfferDto;

import java.util.List;

public interface OfferService {


    OfferDto createNewOffer(OfferDto offerDto);
    OfferDto getOfferById(Long Id);

    List<OfferDto> getAllOffers();
}
