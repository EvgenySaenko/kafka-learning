package com.evgeny.kafka.outbox.api.controller;

import com.evgeny.kafka.outbox.api.dto.CreateOrderRequest;
import com.evgeny.kafka.outbox.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> create(@RequestBody @Valid CreateOrderRequest request) {
        UUID orderId = orderService.createOrder(request.getAmount());
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }
}
