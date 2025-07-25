package org.example.grocery_app.service;

import org.example.grocery_app.entities.FeeTable;

public interface FeeService {

    void  createFees();

    FeeTable getDeliveryFees(Long id);
}
