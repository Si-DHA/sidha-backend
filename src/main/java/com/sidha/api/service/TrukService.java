package com.sidha.api.service;

import com.sidha.api.DTO.TrukMapper;
import com.sidha.api.DTO.request.CreateTrukRequestDTO;
import com.sidha.api.DTO.request.UpdateTrukRequestDTO;
import com.sidha.api.model.Sopir;
import com.sidha.api.model.Truk;
import com.sidha.api.model.UserModel;
import com.sidha.api.repository.TrukDb;
import com.sidha.api.repository.UserDb;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.sidha.api.model.enumerator.Role.SOPIR;

@Service
@AllArgsConstructor
public class TrukService {
    private UserDb userRepository;

    private TrukMapper trukMapper;

    private ModelMapper modelMapper;

    private TrukDb trukDb;

    public Truk createTruk(CreateTrukRequestDTO createTrukRequestDTO) {
        Truk truk = trukMapper.createTrukRequestDTOToTruk(createTrukRequestDTO);
        truk = trukDb.save(truk);
        var idSopir = createTrukRequestDTO.getIdSopir();
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

    // public Truk updateTruk(UpdateTrukRequestDTO updateTrukRequestDTO) {
    //     Truk trukFromDto = trukMapper.updateTrukRequestDTOToTruk(updateTrukRequestDTO);
    //     Truk truk = trukDb.findById(trukFromDto.getIdTruk()).orElse(null);
    //     if (truk != null) {
    //         truk.setLicensePlate(trukFromDto.getLicensePlate());
    //         truk.setMerk(trukFromDto.getMerk());

    //     }
    // }
}
