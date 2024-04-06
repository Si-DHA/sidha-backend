package com.sidha.api.service;

import com.sidha.api.DTO.request.CreatePenawaranHargaRequestDTO;
import com.sidha.api.model.PenawaranHarga;

import java.util.*;

public interface PenawaranHargaService {
    PenawaranHarga getPenawaranHargaById(UUID idPenawaranHarga);

    List<PenawaranHarga> getAllPenawaranHarga();

    PenawaranHarga createPenawaranHarga(CreatePenawaranHargaRequestDTO createPenawaranHargaRequestDTO);

    PenawaranHarga getPenawaranHargaByIdKlien(UUID klien);
}
