package org.example.grocery_app.serviceImplementation;

//package org.example.grocery_app.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.PromoCodeDto;
import org.example.grocery_app.entities.Offer;
import org.example.grocery_app.entities.PromoCode;
import org.example.grocery_app.entities.PromoUsage;
import org.example.grocery_app.entities.User;
//import org.example.grocery_app.repository.PromoCodeRepository;
//import org.example.grocery_app.repository.PromoUsageRepository;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.payload.PromoCodeApplyResponse;
import org.example.grocery_app.payload.PromoCodeResponse;
import org.example.grocery_app.repository.OfferRepository;
import org.example.grocery_app.repository.PromoRepository;
import org.example.grocery_app.repository.PromoUsageRepository;
import org.example.grocery_app.repository.UserRepository;
import org.example.grocery_app.service.PromoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromoServiceImplementation implements PromoService {

    private final PromoRepository promoCodeRepository;
    private final PromoUsageRepository promoUsageRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private  final OfferRepository offerRepository;




    @Override
    public PromoCodeDto createNewPromoCode(PromoCodeDto promoCodeDto) {
        PromoCode promoCode = modelMapper.map(promoCodeDto, PromoCode.class);
//        promoCode.setPromoCodeId(UUID.randomUUID());
        Long offerId = promoCodeDto.getOfferId();
        Offer offer = this.offerRepository.findById(offerId).orElseThrow(() -> new ResourceNotFoundException("offer", "offerId", offerId));

        promoCode.setUsageCount(0);
        // initialize usage count

        promoCode.setOffer(offer);
        this.promoCodeRepository.save(promoCode);
        return this.modelMapper.map(promoCode, PromoCodeDto.class);
    }

    @Override
    public List<PromoCodeDto> getAllPromoCode() {
        return promoCodeRepository.findAll().stream()
                .map(promo -> modelMapper.map(promo, PromoCodeDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PromoCode getPromoCodeById(Long promoCodeId) {
        PromoCode promoCode = promoCodeRepository.findById(promoCodeId)
                .orElseThrow(() -> new RuntimeException("Promo code not found"));
        List<PromoUsage> promoUsages = promoCode.getPromoUsages();
        log.info("promo usage :{}", promoUsages);
        return promoCode;
    }

    @Override
    public PromoCodeDto updatePromoCode(Long promoCodeId, PromoCodeDto promoCodeDto) {
        PromoCode promoCode = promoCodeRepository.findById(promoCodeId)
                .orElseThrow(() -> new RuntimeException("Promo code not found"));

        promoCode.setCode(promoCodeDto.getCode());
        promoCode.setUsageLimit(promoCodeDto.getUsageLimit());
        // Add other fields like discount or expiry if present
        promoCodeRepository.save(promoCode);

        return modelMapper.map(promoCode, PromoCodeDto.class);
    }

    @Override
    public void deletePromoCode(Long promoCodeId) {
        PromoCode promoCode = promoCodeRepository.findById(promoCodeId)
                .orElseThrow(() -> new RuntimeException("Promo code not found"));
        promoCodeRepository.delete(promoCode);
    }

    @Override
    public boolean canUserApplyPromoCode(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Promo code not found"));

        if (promoCode.getUsageCount() >= promoCode.getUsageLimit()) {
            return false;
        }

        boolean alreadyUsed = promoUsageRepository.existsByUserAndPromoCode(user, promoCode);
        return !alreadyUsed;
    }

    @Override
    public void markPromoCodeUsed(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Promo code not found"));

        if (promoCode.getUsageCount() >= promoCode.getUsageLimit()) {
            throw new RuntimeException("Promo code usage limit exceeded");
        }

        boolean alreadyUsed = promoUsageRepository.existsByUserAndPromoCode(user, promoCode);
        if (alreadyUsed) {
            throw new RuntimeException("User has already used this promo code");
        }

        // Save PromoUsage
        PromoUsage usage = new PromoUsage();
//        usage.setPromoUsageId(UUID.randomUUID());
        usage.setUser(user);
        usage.setPromoCode(promoCode);
        usage.setUsed(true);
        promoUsageRepository.save(usage);

        // Increment usage count
        promoCode.setUsageCount(promoCode.getUsageCount() + 1);
        promoCodeRepository.save(promoCode);
    }

    @Override
    public void deactivateExpiredPromoCodes() {
        List<PromoCode> expired = promoCodeRepository.findAll().stream()
                .filter(promo -> promo.getUsageCount() >= promo.getUsageLimit())
                .collect(Collectors.toList());

        for (PromoCode promo : expired) {
            log.info("Deactivating promo: {}", promo.getCode());
            // You can add a field like `active` or `validUntil` to mark it inactive
        }
    }

    @Override
    public PromoCodeApplyResponse applyPromoCode(Long userId, String code) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "userId", userId));
        PromoCode promoCode = this.promoCodeRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("code", "code name:" + code, 0));
         if (promoCode.getUsageCount()>= promoCode.getUsageLimit()){
             throw  new ApiException("Promo code usage limit exceeded || or invalid limit");

         }
        if (promoCode.getExpiryDate() != null && promoCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Promo code has expired");
        }
//         check the usr is already used or not.
        boolean alreadyUsed = this.promoUsageRepository.existsByUserAndPromoCode(user, promoCode);

         if (alreadyUsed){
             throw  new ApiException("User has already used this promo code");
         }
        PromoUsage usage = new PromoUsage();
        usage.setUser(user);
        usage.setPromoCode(promoCode);
        usage.setUsed(true);
        usage.setUsedAt(LocalDateTime.now());
        this.promoUsageRepository.save(usage);

        promoCode.setUsageCount(promoCode.getUsageCount() + 1);
        this.promoCodeRepository.save(promoCode);

        log.info("Promo code '{}' successfully applied by user '{}'", code, userId);

        Offer offer = promoCode.getOffer();

        PromoCodeApplyResponse response = new PromoCodeApplyResponse();
        response.setPromoCode(promoCode.getCode());
        response.setOfferName(offer.getName());
        response.setOfferDescription(offer.getDescription());
        response.setDiscountAmount(offer.getDiscountAmount());
        response.setMessage("Promo code successfully applied!");
        log.info("Promo code '{}' applied for user '{}'", code, userId);
        return response;

    }
}
