package com.sidha.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.request.order.OrderConfirmRequestDTO;
import com.sidha.api.DTO.request.order.UpdateOrderRequestDTO;
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

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable UUID orderId, @RequestHeader("Authorization") String token) {
        token = token.substring(7); // remove "Bearer " from token

        if (authUtils.isKaryawan(token) || authUtils.isKlien(token)) {
            try {
                var response = orderService.getOrderById(orderId);
                return ResponseEntity.ok(new BaseResponse<>(true, 200, "Order fetched successfully", response));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
            }
        }

        return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, "Unauthorized", null));
    }

    // #region Klien

    @GetMapping("/possible-rute")
    public ResponseEntity<?> getAllPossibleRute(@RequestParam("userId") UUID userId){
        try {
            var response = orderService.getAllPossibleRute(userId);
            return ResponseEntity.ok(new BaseResponse<>(true, 200, "Rute fetched successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
        }
    }

    @PostMapping("/price")
    public ResponseEntity<?> getPrice(@RequestBody CreateOrderRequestDTO request,
            @RequestHeader("Authorization") String token) {
        token = token.substring(7); // remove "Bearer " from token

        if (authUtils.isKlien(token) && authUtils.isMatch(token, request.getKlienId())) {
            try {
                var response = orderService.getPrice(request);
                return ResponseEntity.ok(new BaseResponse<>(true, 200, "Price fetched successfully", response));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
            }
        }

        return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, "Unauthorized", null));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequestDTO request,
            @RequestHeader("Authorization") String token) {
        token = token.substring(7); // remove "Bearer " from token

        if (authUtils.isKlien(token) && authUtils.isMatch(token, request.getKlienId())) {
            try {
                var response = orderService.createOrder(request);
                return ResponseEntity.ok(new BaseResponse<>(true, 200, "Order created successfully", response));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
            }
        }

        return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, "Unauthorized", null));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateOrder(@RequestBody UpdateOrderRequestDTO request,
            @RequestHeader("Authorization") String token) {
        token = token.substring(7); // remove "Bearer " from token

        if (authUtils.isKlien(token) && authUtils.isMatch(token, request.getKlienId())) {
            try {
                var response = orderService.updateOrder(request);
                return ResponseEntity.ok(new BaseResponse<>(true, 200, "Order updated successfully", response));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
            }
        }

        return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, "Unauthorized", null));
    }

    @GetMapping("/klien/{klienId}")
    public ResponseEntity<?> getOrdersByKlienId(@PathVariable UUID klienId,
            @RequestHeader("Authorization") String token) {
        token = token.substring(7); // remove "Bearer " from token

        if (authUtils.isKlien(token) && authUtils.isMatch(token, klienId)) {
            try {
                var response = orderService.getOrdersByKlienId(klienId);
                return ResponseEntity.ok(new BaseResponse<>(true, 200, "Orders fetched successfully", response));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
            }
        }

        return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, "Unauthorized", null));
    }

    // #endregion

    // #region Karyawan

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String token) {
        token = token.substring(7); // remove "Bearer " from token

        if (authUtils.isKaryawan(token)) {
            try {
                var response = orderService.getAllOrders();
                return ResponseEntity.ok(new BaseResponse<>(true, 200, "Orders fetched successfully", response));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
            }
        }

        return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, "Unauthorized", null));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmOrder(@RequestBody OrderConfirmRequestDTO request,
            @RequestHeader("Authorization") String token) {
        token = token.substring(7); // remove "Bearer " from token

        if (authUtils.isKaryawan(token) && authUtils.isMatch(token, request.getKaryawanId())) {
            try {
                var response = orderService.confirmOrder(request);
                return ResponseEntity.ok(new BaseResponse<>(true, 200, "Order confirmed successfully", response));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
            }
        }

        return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, "Unauthorized", null));
    }

    // #endregion

}
