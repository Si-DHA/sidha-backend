package com.sidha.api.controller;

import com.sidha.api.DTO.request.CreateTrukRequestDTO;
import com.sidha.api.DTO.request.KonfirmasiBuktiPembayaranDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.Invoice;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.service.InvoiceService;
import com.sidha.api.service.StorageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping("/api/invoice")
public class InvoiceController {

    InvoiceService invoiceService;

    StorageService storageService;

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
            @RequestParam String idInvoice,
            @RequestParam boolean isPelunasan,
            @RequestPart MultipartFile imageFile
    ) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, "Tidak ada bukti yang diunggah", null));
        }

        try {
            Invoice invoice = invoiceService.uploadBuktiPembayaran(UUID.fromString(idInvoice),
                    isPelunasan,
                    imageFile);
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Bukti pembayaran berhasil diunggah", invoice));
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
    public ResponseEntity<?> getImageBuktiPembayaran(
            @RequestParam String idInvoice,
            @RequestParam boolean isPelunasan
    ) {
        try {
            ImageData imageData = invoiceService.getImageBuktiPembayaran(
                    UUID.fromString(idInvoice),
                    isPelunasan);

            if (imageData != null) {
                byte[] image = storageService.getImageFromFileSystem(imageData.getName());

                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.valueOf(imageData.getType()))
                        .body(image);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Bukti pembayaran belum diunggah", null));
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
    public ResponseEntity<?> deleteImageBuktiPembayaran(
            @RequestParam String idInvoice,
            @RequestParam boolean isPelunasan
    ) {
        try {
            invoiceService.deleteImageBuktiPembayaran(
                    UUID.fromString(idInvoice),
                    isPelunasan);

            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Bukti pembayaran berhasil dihapus", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @GetMapping("/{idInvoice}")
    public ResponseEntity<?> getInvoiceById(
            @PathVariable String idInvoice
    ) {
        try {
            Invoice invoice = invoiceService.findInvoiceById(UUID.fromString(idInvoice));
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Invoice is succesfully found", invoice));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }
    }

    @PutMapping("/konfirmasi-bukti")
    public ResponseEntity<?> konfirmasiBuktiPembayaran(
            @Valid @RequestBody KonfirmasiBuktiPembayaranDTO konfirmasiBuktiPembayaranDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getDefaultMessage() + "; ";
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, errorMessages, null));
        }

        try {
            Invoice invoice = invoiceService.konfirmasiBuktiPembayaran(konfirmasiBuktiPembayaranDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Status bukti pembayaran berhasil diperbarui", invoice));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, e.getMessage(), null));
        }

    }
}
