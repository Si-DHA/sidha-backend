package com.sidha.api.service;

import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.PenawaranMapper;
import com.sidha.api.DTO.request.CreatePenawaranDTO;
import com.sidha.api.model.Sopir;
import com.sidha.api.model.Penawaran;
import com.sidha.api.model.UserModel;
import com.sidha.api.repository.PenawaranDb;
import com.sidha.api.repository.UserDb;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PenawaranService {
    private UserDb userRepository;

    private PenawaranMapper penawaranMapper;

    private ModelMapper modelMapper;

    private PenawaranDb penawaranDb;

    public Penawaran createPenawaran(CreatePenawaranDTO createPenawaranDTO) {
        Penawaran penawaran = penawaranMapper.createPenawaranDTOToPenawaran(createPenawaranDTO);
        penawaran = penawaranDb.save(penawaran);
        var idSopir = createPenawaranDTO.getIdSopir();
        if (idSopir != null) {
            UserModel sopir = userRepository.findById(idSopir).orElse(null);
            if (sopir != null && sopir.getRole() == SOPIR) {
                Sopir sopirConverted = modelMapper.map(sopir, Sopir.class);
                truk.setSopir(sopirConverted);
                sopirConverted.setTruk(truk);
            } else {
                System.out.println("nomor 3");
                throw new NoSuchElementException("Id sopir tidak valid");
            }
        }
        return truk;
    }


}
