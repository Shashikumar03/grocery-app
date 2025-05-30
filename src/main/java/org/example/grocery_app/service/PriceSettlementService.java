package org.example.grocery_app.service;

import org.example.grocery_app.dto.HisabBookDto;
import org.example.grocery_app.dto.PriceSettlementDto;

import java.time.LocalDate;
import java.util.List;

public interface PriceSettlementService {

    List<HisabBookDto> doSettlement();

    List<HisabBookDto> getSettlementsByDate(LocalDate date);

    public void updateSettlementStatusByShopOwner(List<Long> ids, boolean paidToShopkeeper);

}
