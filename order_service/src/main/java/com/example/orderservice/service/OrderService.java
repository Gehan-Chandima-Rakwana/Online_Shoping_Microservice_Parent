package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequestDto;

public interface OrderService {
    void placeOrder(OrderRequestDto orderRequestDto);
}
