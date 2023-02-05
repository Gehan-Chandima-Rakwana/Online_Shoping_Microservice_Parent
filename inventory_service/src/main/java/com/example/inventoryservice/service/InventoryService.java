package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponseDto;

import java.util.List;

public interface InventoryService {
    List<InventoryResponseDto> isInStock(List<String> skuCode);
}
