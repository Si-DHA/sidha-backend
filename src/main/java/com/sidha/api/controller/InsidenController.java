package com.sidha.api.controller;

import com.sidha.api.DTO.request.CreateInsidenDTO;
import com.sidha.api.DTO.request.UpdateInsidenDTO;
import com.sidha.api.model.Insiden;
import com.sidha.api.service.InsidenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/insiden")
public class InsidenController {

    @Autowired
    private InsidenService insidenService;

    @PostMapping("/create")
    public ResponseEntity<?> createInsiden(@Valid @ModelAttribute CreateInsidenDTO createInsidenDTO,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Insiden newInsiden = new Insiden();
            newInsiden.setKategori(createInsidenDTO.getKategori());
            newInsiden.setLokasi(createInsidenDTO.getLokasi());
            newInsiden.setKeterangan(createInsidenDTO.getKeterangan());

            Insiden createdInsiden = insidenService.createInsiden(
                    newInsiden, 
                    createInsidenDTO.getSopirId(), 
                    createInsidenDTO.getBuktiFoto());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdInsiden);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateInsiden(@PathVariable UUID id,
                                           @Valid @ModelAttribute UpdateInsidenDTO insidenUpdateDTO,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Insiden updatedInsiden = insidenService.updateInsiden(
                    id, 
                    insidenUpdateDTO, 
                    insidenUpdateDTO.getBuktiFoto());

            return ResponseEntity.ok(updatedInsiden);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update insiden: " + e.getMessage());
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteInsiden(@PathVariable UUID id) {
        insidenService.deleteInsiden(id);
        return ResponseEntity.ok("Insiden is deleted successfully!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInsidenById(@PathVariable UUID id) {
        Insiden insiden = insidenService.getInsidenById(id);
        return ResponseEntity.ok(insiden);
    }
}
