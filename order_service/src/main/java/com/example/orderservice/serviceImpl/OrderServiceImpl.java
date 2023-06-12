package com.example.orderservice.serviceImpl;

import com.example.orderservice.dto.InventoryResponseDto;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.event.OrderPlaceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final WebClient webClient; //already created in config bean

    private final Tracer tracer;// This is from spring cloud sleuth for declare our own span ID

    private final KafkaTemplate<String,OrderPlaceEvent> kafkaTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, WebClient webClient, Tracer tracer, KafkaTemplate<String, OrderPlaceEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;
        this.tracer = tracer;
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public String placeOrder(OrderRequestDto orderRequestDto) {
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

//        //Need to call inventory service and need to check the availability of stock
//        InventoryResponseDto[] inventoryResponseArray = callInventoryService(skuCodes);
//
//        //by default web client make asynchronous request
//        //there for we use .block() to make it as synchronous
//        //synchronous mean it will waite until response come to execute other codes.
//
//        boolean allProductInStock = Arrays.stream(inventoryResponseArray)
//                .allMatch(InventoryResponseDto::isInStock);
//
//        if(allProductInStock) {
//            orderRepository.save(order);
//            return "Success";
//        }else {
//            throw new IllegalArgumentException("Product is not in stock");
//        }

//        Spring cloud sleuth provide us create our own spam ID
//        So In here try to create span id for inventory service
//        Above code is without declare our own span ID [line 61 to 78]
//        Bellow code is with our own span id line [88 to 103]
//        Both are same logic

        Span inventoryServiceLookUp = tracer.nextSpan().name("InventoryServiceLookUp");

        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookUp.start())){
            //Need to call inventory service and need to check the availability of stock
            InventoryResponseDto[] inventoryResponseArray = callInventoryService(skuCodes);

            //by default web client make asynchronous request
            //there for we use .block() to make it as synchronous
            //synchronous mean it will waite until response come to execute other codes.

            boolean allProductInStock = Arrays.stream(inventoryResponseArray)
                    .allMatch(InventoryResponseDto::isInStock);

            if(allProductInStock) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlaceEvent(order.getOrderNumber()));
                return "Success";
            }else {
                throw new IllegalArgumentException("Product is not in stock");
            }

        }finally {
            inventoryServiceLookUp.end();
        }
    }

    private InventoryResponseDto[] callInventoryService(List<String> skuCodes) {
        //Need to call inventory service and need to check the availability of stock
        //instead of webClient.get(), use webClientBuilder.build().get()
        return webClientBuilder.build().get()
                //.uri("http://localhost:8082/api/v1/inventory",
                .uri("http://inventory-service/api/v1/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponseDto[].class)
                .block();
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
