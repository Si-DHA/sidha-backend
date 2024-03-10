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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.sidha.api.model.enumerator.Role.SOPIR;

@Service
@AllArgsConstructor
public class TrukServiceImpl implements TrukService {
    private UserDb userRepository;

    private TrukMapper trukMapper;

    private ModelMapper modelMapper;

    private TrukDb trukDb;

    @Override
    public Truk createTruk(CreateTrukRequestDTO trukFromDto) {
        Truk truk = trukMapper.createTrukRequestDTOToTruk(trukFromDto);
        truk = trukDb.save(truk);
        var idSopir = trukFromDto.getIdSopir();
        if (idSopir != null) {
            UserModel sopir = userRepository.findById(idSopir).orElse(null);
            if (sopir != null && sopir.getRole() == SOPIR) {
                Sopir sopirConverted = modelMapper.map(sopir, Sopir.class);
                truk.setSopir(sopirConverted);
                sopirConverted.setTruk(truk);
            } else {
                throw new NoSuchElementException("Id sopir tidak valid");
            }
        }
        return truk;
    }

    @Override
    public Truk updateTruk(UpdateTrukRequestDTO trukFromDto) {
        Truk truk = trukDb.findById(trukFromDto.getIdTruk()).orElse(null);
        if (truk == null) {
            throw new NoSuchElementException("Id truk tidak valid");
        } else {
            truk.setLicensePlate(trukFromDto.getLicensePlate());
            truk.setMerk(trukFromDto.getMerk());
            truk.setType(trukFromDto.getType());
            truk.setStnkName(trukFromDto.getStnkName());
            truk.setExpiredKir(trukFromDto.getExpiredKir());
            truk.setPanjangBox(trukFromDto.getPanjangBox());
            truk.setLebarBox(trukFromDto.getLebarBox());
            truk.setTinggiBox(trukFromDto.getTinggiBox());
            truk.setKubikasiBox(trukFromDto.getKubikasiBox());
            trukDb.save(truk);
            var idSopir = trukFromDto.getIdSopir();
            if (idSopir != null) {
                UserModel sopir = userRepository.findById(idSopir).orElse(null);
                if (sopir != null && sopir.getRole() == SOPIR) {
                    Sopir sopirConverted = modelMapper.map(sopir, Sopir.class);
                    truk.setSopir(sopirConverted);
                    sopirConverted.setTruk(truk);
                } else {
                    throw new NoSuchElementException("Id sopir tidak valid");
                }
            }
        }

        return truk;
    }

    @Override
    public void deleteTrukById(UUID idTruk) {
        Truk truk = trukDb.findById(idTruk).orElse(null);
        if (truk != null) {
            var sopir = truk.getSopir();
            if (sopir != null)
                sopir.setTruk(null);
            trukDb.deleteById(idTruk);
        } else {
            throw new NoSuchElementException("Id truk tidak valid");
        }
    }

    @Override
    public List<Truk> findAllTruk() {
        return trukDb.findAll();
    }

    @Override
    public Truk findTrukByIdSopir(UUID idSopir) {
        if (idSopir != null) {
            UserModel sopir = userRepository.findById(idSopir).orElse(null);
            if (sopir != null && sopir.getRole() == SOPIR) {
                Sopir sopirConverted = modelMapper.map(sopir, Sopir.class);
                List<Truk> listTruk = trukDb.findBySopir(sopirConverted);
                if (listTruk != null && !listTruk.isEmpty()) {
                    return listTruk.get(0);
                }
            } else {
                throw new NoSuchElementException("Id sopir tidak valid");
            }
        }
        return null;
    }
}
