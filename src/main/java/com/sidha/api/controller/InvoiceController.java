package com.sidha.api.controller;

import com.sidha.api.DTO.request.UploadBuktiPembayaranDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.Invoice;
import com.sidha.api.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping("/api/invoice")
public class InvoiceController {

    InvoiceService invoiceService;

    @PostMapping("/create")
    public ResponseEntity<?> createInvoice() {
        try {
            Invoice invoice = invoiceService.createInvoice();
            return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true, 201, "Invoice is succesfully created", invoice));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }


    @PostMapping("/upload-bukti")
    public ResponseEntity<?> uploadBuktiPembayarn(
            @RequestParam String idInvoice, @RequestParam boolean isPelunasan,
            @RequestPart MultipartFile imageFile
    ) {
        UploadBuktiPembayaranDTO uploadBuktiPembayaranDTO = new UploadBuktiPembayaranDTO(
                UUID.fromString(idInvoice),
                isPelunasan,
                imageFile
        );

        try {
            Invoice invoice = invoiceService.uploadBuktiPembayaran(uploadBuktiPembayaranDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Bukti pembayaran berhasil diunggah", invoice));
        } catch (IOException e) {
            String errorMessage = "Error uploading image: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, errorMessage, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

}
