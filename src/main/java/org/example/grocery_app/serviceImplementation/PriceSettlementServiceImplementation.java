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

        double totalPriceOfAllOrder = 0;

        // Map to accumulate product summaries by product ID
        Map<Long, HisabBookDto> productMap = new HashMap<>();

        for (Order order : completedOrdersToday) {
            double totalPaymentAmount = order.getPayment().getPaymentAmount();
            totalPriceOfAllOrder += totalPaymentAmount;

            for (CartItem item : order.getCart().getCartItems()) {
                Product product = item.getProduct();
                Long productId = product.getId();

                HisabBookDto existing = productMap.getOrDefault(productId, new HisabBookDto());
                existing.setProductId(productId);
                existing.setProductName(product.getName());
                existing.setProductPrice(product.getPrice());
                existing.setProductQuantity(existing.getProductQuantity() + item.getQuantity());
                existing.setTotalPrice(totalPaymentAmount);
                existing.setSettlementDate(LocalDateTime.now());
                productMap.put(productId, existing);
            }
        }

        // You can now collect the product list
       log.info("product of shashi :{}",productMap);

        for(Map.Entry<Long, HisabBookDto> hm :productMap.entrySet()){
            ShopProduct shopProduct = this.shopProductRepository.findByProductId(hm.getKey()).orElseThrow(()-> new ResourceNotFoundException("a","a", hm.getKey()));
            HisabBookDto hisab = hm.getValue();
            hisab.setShopkeeperPrice(shopProduct.getSuggestedPrice());
            hisab.setShopkeeperId(shopProduct.getShopkeeper().getId());


        }
        List<HisabBookDto> hisabBookList = productMap.values().stream().collect(Collectors.toList());
        List<PriceSettlement> priceSettlement = hisabBookList.stream().map(hisabBookDto -> this.modelMapper.map(hisabBookDto, PriceSettlement.class)).collect(Collectors.toList());
        log.info("product of shashi :{}",productMap);

        List<PriceSettlement> priceSettlements = this.priceSettlementRepository.saveAll(priceSettlement);
        List<HisabBookDto> collect = priceSettlements.stream().map(priceSettlement1 -> {
            double payToShopKeeper = priceSettlement1.getPayToShopKeeper();
            double profit = priceSettlement1.getProfit();
            HisabBookDto map = this.modelMapper.map(priceSettlement1, HisabBookDto.class);
            map.setGetProfit(profit);
            map.setPayToShopKeeper(payToShopKeeper);
            return  map;
        }).collect(Collectors.toList());

        // Build the PriceSettlementDto (assuming you have a suitable constructor or setters)
        return collect;
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
