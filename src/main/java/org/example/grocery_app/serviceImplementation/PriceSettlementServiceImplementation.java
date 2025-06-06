package org.example.grocery_app.serviceImplementation;

import com.google.protobuf.MapEntry;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.HisabBookDto;
import org.example.grocery_app.dto.PriceSettlementDto;
import org.example.grocery_app.entities.*;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.OrderRepository;
import org.example.grocery_app.repository.PriceSettlementRepository;
import org.example.grocery_app.repository.ShopProductRepository;
import org.example.grocery_app.service.PriceSettlementService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PriceSettlementServiceImplementation implements PriceSettlementService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PriceSettlementRepository priceSettlementRepository;

    @Autowired
    private ShopProductRepository shopProductRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<HisabBookDto> doSettlement() {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<Order> completedOrdersToday = orderRepository.findCompletedOrdersToday(start, end);
        log.info("Today completed orders: {}", completedOrdersToday);

        List<Long> orderIds = completedOrdersToday.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        Double totalPriceOfAllOrder = 0.0;

        // Map to accumulate product summaries by product ID
        Map<Long, HisabBookDto> productMap = new HashMap<>();

        for (Order order : completedOrdersToday) {
            Double totalPaymentAmount = order.getPayment().getPaymentAmount();
            totalPriceOfAllOrder += totalPaymentAmount;

            for (CartItem item : order.getCart().getCartItems()) {
                Product product = item.getProduct();
                Long productId = product.getId();

                HisabBookDto existing = productMap.getOrDefault(productId, new HisabBookDto());
                existing.setProductId(productId);
                existing.setProductName(product.getName());
                existing.setProductPrice(product.getPrice());
                existing.setProductQuantity(existing.getProductQuantity() + item.getQuantity());
                existing.setTotalPrice(totalPaymentAmount); // Optional: consider accumulating instead
                existing.setSettlementDate(LocalDateTime.now());
                productMap.put(productId, existing);
            }
        }

        log.info("product of shashi :{}", productMap);

        for (Map.Entry<Long, HisabBookDto> entry : productMap.entrySet()) {
            Long productId = entry.getKey();
            HisabBookDto hisab = entry.getValue();

            ShopProduct shopProduct = shopProductRepository.findByProductId(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("ShopProduct", "productId", productId));

            Double suggestedPrice = shopProduct.getSuggestedPrice();
            if (suggestedPrice == null) {
                suggestedPrice = 0.0;
            }

            hisab.setShopkeeperPrice(suggestedPrice);
            if (shopProduct.getShopkeeper() != null) {
                hisab.setShopkeeperId(shopProduct.getShopkeeper().getId());
            } else {
                log.warn("Shopkeeper not found for productId: {}", productId);
                hisab.setShopkeeperId(null);
            }
        }

        List<HisabBookDto> hisabBookList = new ArrayList<>(productMap.values());
        List<PriceSettlement> priceSettlementEntities = hisabBookList.stream()
                .map(dto -> modelMapper.map(dto, PriceSettlement.class))
                .collect(Collectors.toList());

        log.info("product of shashi :{}", productMap);

        try {
            List<PriceSettlement> savedSettlements = priceSettlementRepository.saveAll(priceSettlementEntities);
            log.info("✅ Price settlements saved: {}", savedSettlements);

            return savedSettlements.stream().map(settlement -> {
                HisabBookDto dto = modelMapper.map(settlement, HisabBookDto.class);
                dto.setGetProfit(settlement.getProfit());
                dto.setPayToShopKeeper(settlement.getPayToShopKeeper());
                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("❌ Failed to save price settlements", e);
            return null;
        }
    }

    @Override
    public List<HisabBookDto> getSettlementsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<PriceSettlement> priceSettlements = priceSettlementRepository.findAllBySettlementDateBetween(start, end);

        List<HisabBookDto> collect = priceSettlements.stream().map(priceSettlement1 -> {
            double payToShopKeeper = priceSettlement1.getPayToShopKeeper();
            double profit = priceSettlement1.getProfit();
            HisabBookDto map = this.modelMapper.map(priceSettlement1, HisabBookDto.class);
            map.setGetProfit(profit);
            map.setPayToShopKeeper(payToShopKeeper);
            return  map;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void updateSettlementStatusByShopOwner(List<Long> ids, boolean paidToShopkeeper) {
        if(ids.isEmpty()){
            throw  new ApiException("please select the items");
        }
        List<PriceSettlement> allBills = priceSettlementRepository.findAllById(ids);
        List<PriceSettlement> collect = allBills.stream()
                .filter((bill) -> !bill.isPaidToShopkeeper())
                .map((bill) -> {
                    bill.setPaidToShopkeeper(true);
                    return bill;
                }).collect(Collectors.toList());

        this.priceSettlementRepository.saveAll(collect);
    }



}
