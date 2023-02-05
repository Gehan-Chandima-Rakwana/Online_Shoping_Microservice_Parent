package com.example.productservice;

import com.example.productservice.dto.ProductRequestDto;
import com.example.productservice.dto.ProductResponseDto;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.assertions.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ProductServiceTest{

//    @Container
//    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @MockBean
//    private ProductService productService;
//
//    @DynamicPropertySource
//    static void setProperty(DynamicPropertyRegistry dynamicPropertyRegistry){
//        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
//    }
//
//    @Test
//    void canCreateProduct() throws Exception {
//        ProductRequestDto productRequestDto = getProductRequest();
//        String productRequestString = objectMapper.writeValueAsString(productRequestDto);
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(productRequestString))
//                .andExpect(status().isCreated());
//
//        //Assertions.assertTrue(productRepository.findAll().size() == 1);
//    }
//
//    private ProductRequestDto getProductRequest() {
//        return ProductRequestDto.builder()
//                .name("Iphone 13")
//                .description("Iphone 13")
//                .price(BigDecimal.valueOf(1300))
//                .build();
//    }
//
//    @Test
//    void canRetrieveProduct() throws Exception {
//        //given
//        ProductResponseDto p1 = new ProductResponseDto(
//                "1l",
//                "Iphone 14",
//                "Iphone 14",
//                BigDecimal.valueOf(14000));
//
//        Mockito.when(productService.getAll()).thenReturn(Arrays.asList(p1));
//
//        //when
//        MvcResult result =mockMvc.perform(MockMvcRequestBuilders
//						.get("/api/v1/product")
//						.accept(MediaType.APPLICATION_JSON))
//						.andDo(print())
//						.andExpect(status().isOk())
//                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is("1l")))
//                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Iphone 14")))
//                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is("Iphone 14")))
//                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].price", Matchers.is(14000)))
//						.andReturn();
//        //then
//        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
//
//    }
}
