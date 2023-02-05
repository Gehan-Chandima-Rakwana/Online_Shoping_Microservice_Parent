package com.example.orderservice.serviceImpl;

import com.example.orderservice.dto.InventoryResponseDto;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final WebClient webClient; //already created in config bean
    public OrderServiceImpl(OrderRepository orderRepository, WebClient webClient) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;
    }


    @Override
    public void placeOrder(OrderRequestDto orderRequestDto) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequestDto.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());

        //Need to call inventory service and need to check the availability of stock
        InventoryResponseDto[] inventoryResponseArray = webClient.get()
                                    .uri("http://localhost:8082/api/v1/inventory",
                                            uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                                    .retrieve()
                                    .bodyToMono(InventoryResponseDto[].class)
                                    .block();

                //by default web client make asynchronous request
                //there for we use .block() to make it as synchronous
                //synchronous mean it will waite until response come to execute other codes.

        boolean allProductInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponseDto::isInStock);

        if(allProductInStock)
            orderRepository.save(order);
        else
            throw new IllegalArgumentException("Product is not in stock");
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
