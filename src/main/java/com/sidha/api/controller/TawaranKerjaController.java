package com.sidha.api.controller;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidha.api.DTO.request.AcceptTawaranKerjaDTO;
import com.sidha.api.DTO.request.ConfirmTawaranKerjaDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.TawaranKerja;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.service.TawaranKerjaService;
import com.sidha.api.utils.AuthUtils;

@AllArgsConstructor
@RestController
@RequestMapping("/api/tawaran-kerja")
public class TawaranKerjaController {

    private TawaranKerjaService tawaranKerjaService;
    private AuthUtils authUtils;

    @GetMapping("/available")
    public ResponseEntity<BaseResponse<List<OrderItem>>> getAvailableOrderItems() {
        List<OrderItem> items = tawaranKerjaService.listAvailableOrderItems();
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "Available order items fetched successfully", items));
    }

    @PostMapping("/accept")
    public ResponseEntity<BaseResponse<TawaranKerja>> acceptJobOffer(
            @RequestBody AcceptTawaranKerjaDTO dto) {
        // if (!authUtils.isSopir(dto.getSopirId().toString())) {
        // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new
        // BaseResponse<>(false, 401, "Unauthorized", null));
        // }

        TawaranKerja tawaranKerja = tawaranKerjaService.acceptJobOffer(dto.getSopirId(), dto.getOrderItemId(),
                dto.getLokasi());
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "Job offer accepted successfully", tawaranKerja));
    }

    @PostMapping("/confirm/{tawaranKerjaId}")
    public ResponseEntity<BaseResponse<Void>> confirmJobOffer(
            @PathVariable UUID tawaranKerjaId, @RequestBody ConfirmTawaranKerjaDTO dto) {
        // if (!authUtils.isKaryawan(token)) {
        // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new
        // BaseResponse<>(false, 401, "Unauthorized", null));
        // }

        // UUID karyawanId = authUtils.getUserId(token);
        try {
            tawaranKerjaService.confirmJobOffer(dto.getKaryawanId(), tawaranKerjaId);
            return ResponseEntity.ok(new BaseResponse<>(true, 200, "Job offer confirmed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, 400, e.getMessage(), null));
        }
    }

    @GetMapping("")
    public ResponseEntity<BaseResponse<List<TawaranKerja>>> getAllJobOffers() {
        List<TawaranKerja> offers = tawaranKerjaService.getAllJobOffers();
        return ResponseEntity.ok(new BaseResponse<>(true, 200, "All job offers fetched successfully", offers));
    }

    @GetMapping("/accepted-by-sopir/{sopirId}")
    public ResponseEntity<BaseResponse<List<TawaranKerja>>> getJobOffersAcceptedBySopir(@PathVariable UUID sopirId) {
        List<TawaranKerja> offers = tawaranKerjaService.getJobOffersAcceptedBySopir(sopirId);
        return ResponseEntity
                .ok(new BaseResponse<>(true, 200, "Job offers accepted by driver fetched successfully", offers));
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<?> getTawaranKerjaByOrderItemId(@PathVariable UUID orderItemId) {
        try {
            List<TawaranKerja> tawaranKerja = tawaranKerjaService.getTawaranKerjaByOrderItemId(orderItemId);
            if (tawaranKerja.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(tawaranKerja);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching data");
        }
    }

}
