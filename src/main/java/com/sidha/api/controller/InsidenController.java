package com.sidha.api.controller;

import com.sidha.api.model.Insiden;
import com.sidha.api.model.Insiden.InsidenStatus;
import com.sidha.api.service.InsidenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/insiden")
public class InsidenController {

    @Autowired
    private InsidenService insidenService;

    @PostMapping("/create")
    public ResponseEntity<?> createInsiden(
            @RequestParam("sopirId") UUID sopirId,
            @RequestParam("kategori") String kategori,
            @RequestParam("lokasi") String lokasi,
            @RequestParam("keterangan") String keterangan,
            @RequestPart(value = "buktiFoto", required = false) MultipartFile buktiFoto) {
        try {
            Insiden insiden = new Insiden();
            insiden.setKategori(kategori);
            insiden.setLokasi(lokasi);
            insiden.setKeterangan(keterangan);
            Insiden createdInsiden = insidenService.createInsiden(insiden, sopirId, buktiFoto);
            return new ResponseEntity<>(createdInsiden, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateInsiden(
            @PathVariable UUID id,
            @RequestParam("kategori") String kategori,
            @RequestParam("lokasi") String lokasi,
            @RequestParam("keterangan") String keterangan,
            @RequestPart(value = "buktiFoto", required = false) MultipartFile buktiFoto) {
        try {
            Insiden insidenDetails = new Insiden();
            insidenDetails.setKategori(kategori);
            insidenDetails.setLokasi(lokasi);
            insidenDetails.setKeterangan(keterangan);
            Insiden updatedInsiden = insidenService.updateInsiden(id, insidenDetails, buktiFoto);
            return ResponseEntity.ok(updatedInsiden);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInsiden(@PathVariable UUID id) {
        try {
            insidenService.deleteInsiden(id);
            return ResponseEntity
            .ok("Insiden is deleted!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateInsidenStatus(
            @PathVariable UUID id,
            @RequestParam("status") InsidenStatus status) {
        try {
            Insiden updatedInsiden = insidenService.updateInsidenStatus(id, status);
            return ResponseEntity.ok(updatedInsiden);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Insiden>> getAllInsidens() {
        List<Insiden> insidens = insidenService.getAllInsidens();
        return ResponseEntity.ok(insidens);
    }
}