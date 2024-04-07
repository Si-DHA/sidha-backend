package com.sidha.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.service.OrderService;
import com.sidha.api.utils.AuthUtils;

import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private OrderService orderService;

    private AuthUtils authUtils;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequestDTO request, @RequestHeader("Authorization") String token) {
        token = token.substring(7); // remove "Bearer " from token

        if (authUtils.isKlien(token)) {
            try {
                var response = orderService.createOrder(request, authUtils.getUserId(token));
                return ResponseEntity.ok(new BaseResponse<>(true, 200, "Order created successfully", response));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
            }
        }

        return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, "Unauthorized", null));
    }
}
