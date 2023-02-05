package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryResponseDto;
import com.example.inventoryservice.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    //http://localhost:8082/api/v1/inventory/Iphone_12,Iphone_12_red        =  Path variables
    //http://localhost:8082/api/v1/inventory?skuCode=Iphone_12&skuCode=Iphone_12_red        =  Request parameter
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponseDto> isInStock(@RequestParam List<String> skuCode){
        return inventoryService.isInStock(skuCode);
    }
}
