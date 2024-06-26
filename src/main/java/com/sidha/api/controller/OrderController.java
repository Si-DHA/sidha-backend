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
import org.springframework.web.bind.annotation.*;
import com.sidha.api.DTO.request.order.CreateOrderRequestDTO;
import com.sidha.api.DTO.request.order.OrderConfirmRequestDTO;
import com.sidha.api.DTO.request.order.UpdateOrderRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.order.Order;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.service.OrderService;
import com.sidha.api.service.StorageService;
import com.sidha.api.utils.AuthUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;

import com.sidha.api.model.image.ImageData;
import org.springframework.http.MediaType;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private OrderService orderService;

    private AuthUtils authUtils;

    StorageService storageService;

    // #region Klien
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId, @RequestHeader("Authorization") String token) {
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
    public ResponseEntity<?> getAllPossibleRute(@RequestParam("userId") UUID userId) {
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

    // Endpoint to get total expenditure for a client in a daily range
    @GetMapping("/total-expenditure/daily/{klienId}")
    public ResponseEntity<?> getTotalExpenditureDaily(
            @PathVariable UUID klienId,
            @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            BigDecimal totalExpenditure = orderService.getTotalExpenditureByKlienDaily(klienId, localDate);
            return ResponseEntity
                    .ok(new BaseResponse<>(true, 200, "Total expenditure fetched successfully", totalExpenditure));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
        }
    }

    // Endpoint to get total expenditure for a client in a monthly range
    @GetMapping("/total-expenditure/monthly/{klienId}")
    public ResponseEntity<?> getTotalExpenditureMonthly(
            @PathVariable UUID klienId,
            @RequestParam String month,
            @RequestParam String year) {
        try {
            YearMonth yearMonth = YearMonth.parse(year + "-" + month, DateTimeFormatter.ofPattern("yyyy-MM"));
            BigDecimal totalExpenditure = orderService.getTotalExpenditureByKlienMonthly(klienId, yearMonth);
            return ResponseEntity
                    .ok(new BaseResponse<>(true, 200, "Total expenditure fetched successfully", totalExpenditure));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
        }
    }

    // Endpoint to get total expenditure for a client in a yearly range
    @GetMapping("/total-expenditure/yearly/{klienId}")
    public ResponseEntity<?> getTotalExpenditureYearly(
            @PathVariable UUID klienId,
            @RequestParam String year) {
        try {
            Year localDate = Year.parse(year + "-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            BigDecimal totalExpenditure = orderService.getTotalExpenditureByKlienYearly(klienId, localDate);
            return ResponseEntity
                    .ok(new BaseResponse<>(true, 200, "Total expenditure fetched successfully", totalExpenditure));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
        }
    }

    // Endpoint to get total expenditure for a client for today
    @GetMapping("/total-expenditure/daily-now/{klienId}")
    public ResponseEntity<?> getTotalExpenditureDailyNow(
            @PathVariable UUID klienId) {
        try {
            LocalDate currentDate = LocalDate.now();
            BigDecimal totalExpenditure = orderService.getTotalExpenditureByKlienDaily(klienId, currentDate);
            return ResponseEntity
                    .ok(new BaseResponse<>(true, 200, "Total expenditure fetched successfully", totalExpenditure));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
        }
    }

    // Endpoint to get total expenditure for a client for this month
    @GetMapping("/total-expenditure/monthly-now/{klienId}")
    public ResponseEntity<?> getTotalExpenditureMonthlyNow(
            @PathVariable UUID klienId) {
        try {
            YearMonth currentYearMonth = YearMonth.now();
            BigDecimal totalExpenditure = orderService.getTotalExpenditureByKlienMonthly(klienId, currentYearMonth);
            return ResponseEntity
                    .ok(new BaseResponse<>(true, 200, "Total expenditure fetched successfully", totalExpenditure));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
        }
    }

    // Endpoint to get total expenditure for a client for this year
    @GetMapping("/total-expenditure/yearly-now/{klienId}")
    public ResponseEntity<?> getTotalExpenditureYearlyNow(
        @PathVariable UUID klienId) {
        try {
            Year currentYear = Year.now();
            BigDecimal totalExpenditure = orderService.getTotalExpenditureByKlienYearly(klienId, currentYear);
            return ResponseEntity.ok(new BaseResponse<>(true, 200, "Total expenditure fetched successfully", totalExpenditure));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse<>(false, 400, e.getMessage(), null));
        }
    }

    @GetMapping(value = "/klien/{klienId}/order-item")
    public List<OrderItem> getAllOrderItemDiprosesByKlienId(@PathVariable("klienId") UUID klienId) {
        try {
            List<OrderItem> listOrderItem = orderService.getAllOrderItemDiprosesByKlienId(klienId);
            return listOrderItem;
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item tidak ditemukan");
        }
    }

    @GetMapping("/count-completed/{klienId}")
    public ResponseEntity<Integer> countCompletedOrderItemsByKlienId(@PathVariable UUID klienId) {
        int count = orderService.countCompletedOrderItemsByKlienId(klienId);
        return ResponseEntity.ok(count);
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

    @GetMapping("/by-order-item/{idOrderItem}")
    public ResponseEntity<?> getOrderByIdOrderItem(@PathVariable String idOrderItem) {
        try {
            var response = orderService.getOrderByOrderItem(idOrderItem);
            return ResponseEntity.ok(new BaseResponse<>(true, 200, "Order fetched successfully", response));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, 400, e.getMessage(), null));
        }
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

    // #region Sopir
    @PostMapping("/upload-bukti-muat")
    public ResponseEntity<?> uploadBuktiMuat(
            @RequestParam String idOrderItem,
            @RequestPart MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, "Tidak ada bukti yang diunggah", null));
        }

        try {
            OrderItem orderItem = orderService.uploadImageMuat(idOrderItem,
                    imageFile);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(true, 200, "Bukti berhasil diunggah", orderItem));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (IOException e) {
            String errorMessage = "Error uploading image: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, errorMessage, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @PostMapping("/upload-bukti-bongkar")
    public ResponseEntity<?> uploadBuktiBongkar(
            @RequestParam String idOrderItem,
            @RequestPart MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, "Tidak ada bukti yang diunggah", null));
        }

        try {
            OrderItem orderItem = orderService.uploadImageBongkar(idOrderItem,
                    imageFile);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(true, 200, "Bukti berhasil diunggah", orderItem));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (IOException e) {
            String errorMessage = "Error uploading image: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, errorMessage, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @GetMapping("/get-bukti-muat")
    public ResponseEntity<?> getImageBuktiMuat(
            @RequestParam String idOrderItem) {
        try {
            ImageData imageData = orderService.getImageMuat(
                    idOrderItem);

            if (imageData != null) {
                byte[] image = storageService.getImageFromFileSystem(imageData.getName());

                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.valueOf(imageData.getType()))
                        .body(image);
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new BaseResponse<>(true, 200, "Bukti belum diunggah", null));
            }

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (IOException e) {
            String errorMessage = "Error fetching image: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, errorMessage, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @GetMapping("/get-bukti-bongkar")
    public ResponseEntity<?> getImageBuktiBongkar(
            @RequestParam String idOrderItem) {
        try {
            ImageData imageData = orderService.getImageBongkar(
                    idOrderItem);

            if (imageData != null) {
                byte[] image = storageService.getImageFromFileSystem(imageData.getName());

                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.valueOf(imageData.getType()))
                        .body(image);
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new BaseResponse<>(true, 200, "Bukti belum diunggah", null));
            }

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (IOException e) {
            String errorMessage = "Error fetching image: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, errorMessage, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete-bukti-muat")
    public ResponseEntity<?> deleteImageBuktiMuat(
            @RequestParam String idOrderItem) {
        try {
            orderService.deleteImageMuat(
                    idOrderItem);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(true, 200, "Bukti berhasil dihapus", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete-bukti-bongkar")
    public ResponseEntity<?> deleteImageBuktiBongkar(
            @RequestParam String idOrderItem) {
        try {
            orderService.deleteImageBongkar(
                    idOrderItem);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(true, 200, "Bukti berhasil dihapus", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @GetMapping(value = "/sopir/{sopir}/view-all")
    public List<OrderItem> getAllOrderItemByIdSopir(@PathVariable("sopir") String sopir) {
        List<OrderItem> listOrderItem = orderService.getAllOrderItemByIdSopir(UUID.fromString(sopir));
        return listOrderItem;
    }

    @GetMapping(value = "/klien/{klien}/view-all")
    public List<Order> getAllOrderByIdKlien(@PathVariable("klien") String klien) {
        List<Order> listOrder = orderService.getOrdersByKlienId(UUID.fromString(klien));
        return listOrder;
    }

    @GetMapping("/order-item/{idOrderItem}")
    public ResponseEntity<?> getOrderItemById(
            @PathVariable String idOrderItem) {
        try {
            OrderItem orderItem = orderService.getOrderItemById(idOrderItem);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(true, 200, "Order item is succesfully found", orderItem));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @GetMapping(value = "/{idOrder}/order-item")
    public List<OrderItem> getAllOrderItemByIdOrder(@PathVariable("idOrder") String idOrder) {
        try {
            List<OrderItem> listOrderItem = orderService.getAllOrderItemByIdOrder(idOrder);
            return listOrderItem;
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item tidak ditemukan");
        }
    }

}
