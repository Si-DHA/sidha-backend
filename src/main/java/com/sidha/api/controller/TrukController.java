package com.sidha.api.controller;

import com.sidha.api.DTO.request.CreateTrukRequestDTO;
import com.sidha.api.model.Truk;
import com.sidha.api.service.TrukService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/truk")
public class TrukController {

    TrukService trukService;

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
