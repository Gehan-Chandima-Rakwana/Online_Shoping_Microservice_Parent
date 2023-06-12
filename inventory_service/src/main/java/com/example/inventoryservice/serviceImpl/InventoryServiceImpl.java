package com.example.inventoryservice.serviceImpl;

import com.example.inventoryservice.dto.InventoryResponseDto;
import com.example.inventoryservice.repository.InventoryRepository;
import com.example.inventoryservice.service.InventoryService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
   // @SneakyThrows // this is used for handle Thread.sleep function throw exception handle. because it is only used when test. this SneakyThrows is not good for production
    public List<InventoryResponseDto> isInStock(List<String> skuCode) {
            // this code Segment used for the order service circuit breaker example
            // time out function test
            // It is not necessary code only used to check time out working or not
//        log.info("Wait started");
//        Thread.sleep(10000);      // need to uncomment this line when testing Timelimiter and retry functions in order exerciser
//        log.info("Wait Ended");

        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                    InventoryResponseDto.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build()
                )
                .collect(Collectors.toList());
    }
}
