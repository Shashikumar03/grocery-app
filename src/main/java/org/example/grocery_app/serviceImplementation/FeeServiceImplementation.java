package org.example.grocery_app.serviceImplementation;

import org.example.grocery_app.entities.FeeTable;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.FeeTableRepository;
import org.example.grocery_app.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeeServiceImplementation implements FeeService {

    @Autowired
    private FeeTableRepository feeTableRepository;


    @Override
    public void createFees() {
        FeeTable feeTable = new FeeTable();
        this.feeTableRepository.save(feeTable);
    }

    @Override
    public int getDeliveryFees(Long id) {
        FeeTable feeTable = this.feeTableRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feed", "FeeId", id));
        return  feeTable.getDeliveryCharges();

//        return  deliveryCharges;
    }
}
