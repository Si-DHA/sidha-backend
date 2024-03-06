package com.sidha.api.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sidha.api.DTO.request.CreatePenawaranDTO;
import com.sidha.api.model.Penawaran;
import com.sidha.api.service.PenawaranService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/quotation")
public class PenawaranController {

    PenawaranService penawaranService;

    @PostMapping("/create")
    public ResponseEntity<Truk> createTruk(@Valid @RequestBody CreateTrukRequestDTO createTrukRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors ) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessages
            );
        }

        try {
            Truk truk = trukService.createTruk(createTrukRequestDTO);
            return ResponseEntity.ok(truk);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
        }
    }
}
