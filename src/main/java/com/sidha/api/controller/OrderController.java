package com.sidha.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.request.order.OrderConfirmRequestDTO;
import com.sidha.api.DTO.request.order.UpdateOrderRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.service.OrderService;
import com.sidha.api.service.StorageService;
import com.sidha.api.utils.AuthUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import java.util.NoSuchElementException;
import java.io.IOException;
import com.sidha.api.model.image.ImageData;
import org.springframework.http.MediaType;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private OrderService orderService;

    private AuthUtils authUtils;

    StorageService storageService;

    //#region Klien

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequestDTO request, @RequestHeader("Authorization") String token) {
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

    @PostMapping("/update")
    public ResponseEntity<?> updateOrder(@RequestBody UpdateOrderRequestDTO request, @RequestHeader("Authorization") String token) {
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

    @GetMapping("/{klienId}")
    public ResponseEntity<?> getOrdersByKlienId(@PathVariable UUID klienId, @RequestHeader("Authorization") String token) {
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

    //#endregion

    //#region Karyawan

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
    public ResponseEntity<?> confirmOrder(@RequestBody OrderConfirmRequestDTO request, @RequestHeader("Authorization") String token) {
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

    //#endregion
    
    //#region Sopir
    @PostMapping("/upload-bukti")
    public ResponseEntity<?> uploadBuktiBongkarMuat(
            @RequestParam String idOrderItem,
            @RequestParam boolean isBongkar,
            @RequestPart MultipartFile imageFile
    ) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, "Tidak ada bukti yang diunggah", null));
        }

        try {
            OrderItem orderItem = orderService.uploadImageBongkarMuat(UUID.fromString(idOrderItem),
                    isBongkar,
                    imageFile);
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Bukti berhasil diunggah", orderItem));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (IOException e) {
            String errorMessage = "Error uploading image: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, errorMessage, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @GetMapping("/get-bukti")
    public ResponseEntity<?> getImageBuktiBongkarMuat(
            @RequestParam String idOrderItem,
            @RequestParam boolean isBongkar
    ) {
        try {
            ImageData imageData = orderService.getImageBongkarMuat(
                    UUID.fromString(idOrderItem),
                    isBongkar);

            if (imageData != null) {
                byte[] image = storageService.getImageFromFileSystem(imageData.getName());

                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.valueOf(imageData.getType()))
                        .body(image);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Bukti belum diunggah", null));
            }

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (IOException e) {
            String errorMessage = "Error fetching image: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, errorMessage, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete-bukti")
    public ResponseEntity<?> deleteImageBuktiBongkarMuat(
            @RequestParam String idOrderItem,
            @RequestParam boolean isBongkar
    ) {
        try {
            orderService.deleteImageBongkarMuat(
                    UUID.fromString(idOrderItem),
                    isBongkar);

            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Bukti berhasil dihapus", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    //#endregion
}
