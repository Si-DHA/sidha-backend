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
    public ResponseEntity<?> createTruk(@Valid @RequestBody CreateTrukRequestDTO createTrukRequestDTO,
                                           BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, errorMessages, null));
        }

        try {
            Truk truk = trukService.createTruk(createTrukRequestDTO);
            trukDb.save(truk);
            return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true, 201, "Truck is succesfully created", truk));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateTruk(@Valid @RequestBody UpdateTrukRequestDTO updateTrukRequestDTO,
                                           BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(false, 500, errorMessages, null));
        }

        try {
            Truk truk = trukService.updateTruk(updateTrukRequestDTO);
            trukDb.save(truk);
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Truck is succesfully updated", truk));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{idTruk}")
    public ResponseEntity<?> deleteTruk(@PathVariable("idTruk") UUID idTruk) {
        try {
            trukService.deleteTrukById(idTruk);
            String message = "Truk with ID " + idTruk + " is successfully deleted!";
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Truck is succesfully deleted", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        }
    }

    @GetMapping("/view-all")
    public ResponseEntity<?> viewAllTruk() {
        List<Truk> listTruk = new ArrayList<>();
        listTruk = trukService.findAllTruk();
        if (listTruk.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(true, 404, "No truck data found", null));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Truck list is succesfully found", listTruk));
        }
    }

    @GetMapping("/view/{idTruk}")
    public ResponseEntity<?> viewTrukByIdTruk(@PathVariable("idTruk") UUID idTruk) {
        Truk truk = trukService.findTrukByIdTruk(idTruk);
        if (truk == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, "No truck data found", null));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Truck data is succesfully found", truk));
        }
    }

    @GetMapping("/view-sopir/{idSopir}")
    public ResponseEntity<?> viewTrukByIdSopir(@PathVariable("idSopir") UUID idSopir) {
        try {
            Truk truk = trukService.findTrukByIdSopir(idSopir);
            if (truk == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, "No truck data found", null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, 200, "Truck data is succesfully found", truk));
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, 404, e.getMessage(), null));
        }
    }

}
