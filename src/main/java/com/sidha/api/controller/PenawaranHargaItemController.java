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
import com.sidha.api.DTO.request.UpdatePenawaranHargaItemRequestDTO;
import com.sidha.api.model.*;
import com.sidha.api.repository.PenawaranHargaItemDb;

import java.util.*;

@RestController
@RequestMapping("/api")
public class PenawaranHargaItemController {
    @Autowired
    PenawaranHargaItemService penawaranHargaItemService;

    @Autowired
    PenawaranHargaItemDb penawaranHargaItemDb;

    @GetMapping(value = "/penawaran-harga-item/{idPenawaranHargaItem}")
    public PenawaranHargaItem getPenawaranHargaItemById(
            @PathVariable("idPenawaranHargaItem") String idPenawaranHargaItem) {
        try {
            return penawaranHargaItemService.getPenawaranHargaItemById(UUID.fromString(idPenawaranHargaItem));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Penawaran harga item tidak ditemukan");
        }
    }

    @GetMapping(value = "/penawaran-harga-item/{idPenawaranHarga}/view-all")
    public List<PenawaranHargaItem> getAllPenawaranHargaItemByIdPenawaranHarga(
            @PathVariable("idPenawaranHarga") UUID idPenawaranHarga) {
        List<PenawaranHargaItem> listPenawaranHargaItem = penawaranHargaItemService
                .getAllPenawaranHargaItemByIdPenawaranHarga(idPenawaranHarga);
        return listPenawaranHargaItem;
    }

    @PostMapping("/penawaran-harga-item/create")
    public ResponseEntity<PenawaranHargaItem> createPenawaranHargaItem(
            @Valid @RequestBody CreatePenawaranHargaItemRequestDTO createPenawaranHargaItemRequestDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessages);
        }

        try {
            PenawaranHargaItem penawaranHargaItem = penawaranHargaItemService
                    .createPenawaranHargaItem(createPenawaranHargaItemRequestDTO);
            penawaranHargaItemDb.save(penawaranHargaItem);
            return ResponseEntity.ok(penawaranHargaItem);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating PenawaranHargaItem: " + e.getMessage());
        }
    }

    @GetMapping(value = "/penawaran-harga-item/source/{source}/view-all")
    public List<PenawaranHargaItem> getAllPenawaranHargaItemBySource(@PathVariable("source") String source) {
        List<PenawaranHargaItem> listPenawaranHargaItem = penawaranHargaItemService
                .getAllPenawaranHargaItemBySource(source);
        return listPenawaranHargaItem;
    }

    @GetMapping(value = "/penawaran-harga-item/klien/{klien}/view-all")
    public List<PenawaranHargaItem> getAllPenawaranHargaItemByIdKlien(@PathVariable("klien") String klien) {
        List<PenawaranHargaItem> listPenawaranHargaItem = penawaranHargaItemService
                .getAllPenawaranHargaItemByIdKlien(UUID.fromString(klien));
        return listPenawaranHargaItem;
    }

    @PutMapping("/penawaran-harga-item/update")
    public ResponseEntity<PenawaranHargaItem> updatePenawaranHargaItem(
            @Valid @RequestBody UpdatePenawaranHargaItemRequestDTO updatePenawaranHargaItemRequestDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessages);
        }

        try {
            PenawaranHargaItem updatedItem = penawaranHargaItemService
                    .updatePenawaranHargaItem(updatePenawaranHargaItemRequestDTO);
            return ResponseEntity.ok(updatedItem);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    @DeleteMapping("/penawaran-harga-item/delete/{idPenawaranHargaItem}")
    public ResponseEntity<String> deletePenawaranHargaItem(@PathVariable UUID idPenawaranHargaItem) {
        try {
            penawaranHargaItemService.deletePenawaranHargaItem(idPenawaranHargaItem);
            return ResponseEntity
                    .ok("Penawaran Harga Item is deleted successfully!");
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }
}