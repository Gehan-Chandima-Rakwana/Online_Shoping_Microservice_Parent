package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequestDto;

public interface OrderService {
    String placeOrder(OrderRequestDto orderRequestDto);
}
