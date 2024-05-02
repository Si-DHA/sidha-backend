package com.sidha.api.DTO.request;

import com.sidha.api.model.Insiden.InsidenStatus;
import com.sidha.api.model.image.ImageData;
import java.time.LocalDateTime;
import java.util.UUID;

public class InsidenDTO {
    private UUID id;
    private String kategori;
    private String lokasi;
    private String keterangan;
    private UUID sopirId;
    private String sopirName;
    private ImageData buktiFoto;
    private InsidenStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String source;
    private String destination;

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public UUID getSopirId() {
        return sopirId;
    }

    public void setSopirId(UUID sopirId) {
        this.sopirId = sopirId;
    }

    public String getSopirName() {
        return sopirName;
    }

    public void setSopirName(String sopirName) {
        this.sopirName = sopirName;
    }

    public ImageData getBuktiFoto() {
        return buktiFoto;
    }

    public void setBuktiFoto(ImageData buktiFoto) {
        this.buktiFoto = buktiFoto;
    }

    public InsidenStatus getStatus() { 
        return status;
    }

    public void setStatus(InsidenStatus status) { 
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
