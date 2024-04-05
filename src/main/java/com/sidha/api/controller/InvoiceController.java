package com.sidha.api.controller;

import com.sidha.api.DTO.request.CreateTrukRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.Invoice;
import com.sidha.api.model.Truk;
import com.sidha.api.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

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
}
