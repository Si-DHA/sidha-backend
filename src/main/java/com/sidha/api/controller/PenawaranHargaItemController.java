package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.sidha.api.service.PenawaranHargaItemService;

import jakarta.validation.Valid;

import com.sidha.api.DTO.request.CreatePenawaranHargaItemRequestDTO;
import com.sidha.api.model.*;
import java.util.*;

import javax.xml.catalog.Catalog;

@RestController
@RequestMapping("/api")
public class PenawaranHargaItemController {
    @Autowired
    PenawaranHargaItemService penawaranHargaItemService;

    @GetMapping(value="/penawaran-harga-item/{idPenawaranHargaItem}")
    public PenawaranHargaItem getPenawaranHargaItemById(@PathVariable("idPenawaranHargaItem") String idPenawaranHargaItem){
        try{
            return penawaranHargaItemService.getPenawaranHargaItemById(UUID.fromString(idPenawaranHargaItem));
        }
        catch (NoSuchElementException e){
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Penawaran harga item tidak ditemukan"
            );
        }
    }

    @GetMapping(value="/penawaran-harga/{idPenawaranHarga}/penawaran-harga-item/view-all")
    public List<PenawaranHargaItem> getAllPenawaranHargaItemByIdPenawaranHarga(@PathVariable("idPenawaranHarga") UUID idPenawaranHarga){
        List<PenawaranHargaItem> listPenawaranHargaItem = penawaranHargaItemService.getAllPenawaranHargaItemByIdPenawaranHarga(idPenawaranHarga);
        return listPenawaranHargaItem;
    }

    @PostMapping("/penawaran-harga-item/create")
    public ResponseEntity<PenawaranHargaItem> createPenawaranHargaItem(@Valid @RequestBody CreatePenawaranHargaItemRequestDTO createPenawaranHargaItemRequestDTO,
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
            PenawaranHargaItem penawaranHargaItem = penawaranHargaItemService.createPenawaranHargaItem(createPenawaranHargaItemRequestDTO);
            return ResponseEntity.ok(penawaranHargaItem);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating PenawaranHargaItem: " + e.getMessage()
            );
        }
    }
}
