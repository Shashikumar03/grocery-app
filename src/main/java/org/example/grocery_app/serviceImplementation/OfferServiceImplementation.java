package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.dto.OfferDto;
import org.example.grocery_app.entities.Offer;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.OfferRepository;
import org.example.grocery_app.service.OfferService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfferServiceImplementation implements OfferService {

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public OfferDto createNewOffer(OfferDto offerDto) {
        Offer offer = this.modelMapper.map(offerDto, Offer.class);
        Offer saveOffer = this.offerRepository.save(offer);
       return  this.modelMapper.map(saveOffer, OfferDto.class);
    }

    @Override
    public OfferDto getOfferById(Long offerId) {
        Offer offer = this.offerRepository.findById(offerId).orElseThrow(() -> new ResourceNotFoundException("offer", "offerId", offerId));
        return this.modelMapper.map(offer, OfferDto.class);
    }

    @Override
    public List<OfferDto> getAllOffers() {
        List<Offer> allOffer = this.offerRepository.findAll();
        return allOffer.stream().map(offer -> this.modelMapper.map(offer, OfferDto.class)).collect(Collectors.toList());

    }
}
