package com.sidha.api.controller;

import com.sidha.api.DTO.request.CreateTrukRequestDTO;
import com.sidha.api.DTO.request.UpdateTrukRequestDTO;
import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.model.Truk;
import com.sidha.api.repository.TrukDb;
import com.sidha.api.service.TrukService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/truk")
public class TrukController {

    TrukService trukService;

    TrukDb trukDb;

    @PostMapping("/create")
    public ResponseEntity<Truk> createTruk(@Valid @RequestBody CreateTrukRequestDTO createTrukRequestDTO,
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
            Truk truk = trukService.createTruk(createTrukRequestDTO);
            trukDb.save(truk);
            return ResponseEntity.ok(truk);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Truk> updateTruk(@Valid @RequestBody UpdateTrukRequestDTO updateTrukRequestDTO,
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
            Truk truk = trukService.updateTruk(updateTrukRequestDTO);
            trukDb.save(truk);
            return ResponseEntity.ok(truk);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    @DeleteMapping("/delete/{idTruk}")
    public BaseResponse<String> deleteTruk(@PathVariable("idTruk") UUID idTruk) {
        try {
            trukService.deleteTrukById(idTruk);
            String message = "Truk with ID " + idTruk + " is successfully deleted!";
            return new BaseResponse<>(true, 200, message, null);
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, 404, e.getMessage(), null);
        }
    }

    @GetMapping("/view-all")
    public BaseResponse<List<Truk>> viewAllTruk() {
        List<Truk> listTruk = new ArrayList<>();
        listTruk = trukService.findAllTruk();
        if (listTruk.isEmpty()) {
            return new BaseResponse<>(true, 404, "No truck data found", null);
        } else {
            return new BaseResponse<>(true, 200, "Truck list is succesfully found", listTruk);
        }
    }

    @GetMapping("/view-sopir/{idSopir}")
    public BaseResponse<Truk> viewTrukbyIdSopir(@PathVariable("idSopir") UUID idSopir) {
        try {
            Truk truk = trukService.findTrukByIdSopir(idSopir);
            if (truk == null) {
                return new BaseResponse<>(true, 404, "No truck data found", null);
            } else {
                return new BaseResponse<>(true, 200, "Truck data is successfully found", truk);
            }
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, 400, e.getMessage(), null);
        }
    }

}
