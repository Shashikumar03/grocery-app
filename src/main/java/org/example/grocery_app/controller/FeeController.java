package org.example.grocery_app.controller;

import org.example.grocery_app.entities.FeeTable;
import org.example.grocery_app.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fee")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @PostMapping("/")
    public  void createFees(){
        this.feeService.createFees();

    }

    @GetMapping("/delivery/{id}")
    public ResponseEntity<FeeTable> getDeliveryFees(@PathVariable Long id){
        FeeTable deliveryFees = this.feeService.getDeliveryFees(id);

        return  new ResponseEntity<>(deliveryFees, HttpStatus.OK);

    }
}
