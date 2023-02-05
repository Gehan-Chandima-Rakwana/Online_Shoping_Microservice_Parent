package com.example.productservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public class BaseTest {
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    static void setProperty(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    static {
        mongoDBContainer.start();
    }
}
