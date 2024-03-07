package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.sidha.api.service.PenawaranHargaService;

import jakarta.validation.Valid;

import com.sidha.api.DTO.request.CreatePenawaranHargaRequestDTO;
import com.sidha.api.model.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class PenawaranHargaController {
    @Autowired
    PenawaranHargaService penawaranHargaService;

    @GetMapping(value="/penawaran-harga/view-all")
    private List<PenawaranHarga> getAllPenawaranHarga(){
        return penawaranHargaService.getAllPenawaranHarga();
    }

    @GetMapping(value="/penawaran-harga/{idPenawaranHarga}")
    public PenawaranHarga getPenawaranHargaById(@PathVariable("idPenawaranHarga") String idPenawaranHarga){
        try{
            return penawaranHargaService.getPenawaranHargaById(UUID.fromString(idPenawaranHarga));
        }
        catch (NoSuchElementException e){
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Penawaran harga tidak ditemukan"
            );
        }
    }

    @PostMapping(value="/penawaran-harga/create")
    public ResponseEntity<PenawaranHarga> createPenawaranHarga(@Valid @RequestBody CreatePenawaranHargaRequestDTO createPenawaranHargaRequestDTO,
                                                               BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessages
            );
        }

        try {
            PenawaranHarga penawaranHarga = penawaranHargaService.createPenawaranHarga(createPenawaranHargaRequestDTO);
            return ResponseEntity.ok(penawaranHarga);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating Penawaran Harga: " + e.getMessage()
            );
        }
    }
}
