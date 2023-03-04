package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /***
     * Applying  @CircuitBreaker annotation automatically apply CircuitBreaker patten to the method
     * In there we need to provide attributes as name and can provide fallbackMethod
     * when CircuitBreaker open stage (Not working) will call this fallbackMethod
     * It should same as the original method and need to add additional attribute as RuntimeException
     * and there we can do anything when call back to this method
     * and also you cn check actuator health status using http://localhost:8081/actuator/health
     * "inventory" name came from the application property file you can define any name but both name should be same
     * A CompltableFuture is used for asynchronous programming.
     * Then It run on the different thread
     * Retry method : try to call inventory service 3 times(application.property) after waiting 5s(application.property)
     * Note: if you need to test Time limiter and retry functions please uncomment inventory service
     *       time sleep code segment in inventoryServiceImpl function "isInStock"
     * @param orderRequestDto
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory",fallbackMethod = "fallbackMethod") // this is for shutdown
    @TimeLimiter(name = "inventory") // this is for time limiter
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequestDto orderRequestDto){
        return CompletableFuture.supplyAsync(()->orderService.placeOrder(orderRequestDto));
    }

    public CompletableFuture<String> fallbackMethod(OrderRequestDto orderRequestDto, RuntimeException e){
        return CompletableFuture.supplyAsync(()->"Inventory Service Down:");
    }

}
