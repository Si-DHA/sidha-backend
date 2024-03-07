package com.sidha.api.service;

import com.sidha.api.DTO.request.CreatePenawaranHargaItemRequestDTO;
import com.sidha.api.model.PenawaranHargaItem;

import jakarta.validation.Valid;

import java.util.*;

public interface PenawaranHargaItemService {
    PenawaranHargaItem getPenawaranHargaItemById(UUID idPenawaranHargaItem);

    List<PenawaranHargaItem> getAllPenawaranHargaItemByIdPenawaranHarga(UUID idPenawaranHarga);

    PenawaranHargaItem createPenawaranHargaItem( @Valid CreatePenawaranHargaItemRequestDTO createPenawaranHargaItemRequestDTO);

    List<PenawaranHargaItem> getAllPenawaranHargaItemBySource(String source);
    
    List<PenawaranHargaItem> getAllPenawaranHargaItemByIdKlien(UUID klien);
}
