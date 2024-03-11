package com.sidha.api.service;

import com.sidha.api.DTO.request.CreateTrukRequestDTO;
import com.sidha.api.DTO.request.UpdateTrukRequestDTO;
import com.sidha.api.model.Sopir;
import com.sidha.api.model.Truk;

import java.util.List;
import java.util.UUID;

public interface TrukService {
    Truk createTruk(CreateTrukRequestDTO trukFromDto);

    Truk updateTruk(UpdateTrukRequestDTO trukFromDto);

    void deleteTrukById(UUID idTruk);

    List<Truk> findAllTruk();

    Truk findTrukByIdSopir(UUID idSopir);

    Truk findTrukByIdTruk(UUID idTruk);

    void updateSopir(Sopir sopir);

}
