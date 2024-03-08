package com.sidha.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.*;

import com.sidha.api.DTO.PenawaranHargaMapper;
import com.sidha.api.DTO.request.CreatePenawaranHargaRequestDTO;
import com.sidha.api.model.Klien;
import com.sidha.api.model.PenawaranHarga;
import com.sidha.api.model.UserModel;
import com.sidha.api.repository.PenawaranHargaDb;
import com.sidha.api.repository.UserDb;
import static com.sidha.api.model.enumerator.Role.KLIEN;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class PenawaranHargaServiceImpl implements PenawaranHargaService {

    PenawaranHargaDb penawaranHargaDb;
    private UserDb userRepository;
    private ModelMapper modelMapper;
    private PenawaranHargaMapper penawaranHargaMapper;

    @Override
    public List<PenawaranHarga> getAllPenawaranHarga() {
        return penawaranHargaDb.findAll();
    }

    @Override
    public PenawaranHarga getPenawaranHargaById(UUID idPenawaranHarga) {
        return penawaranHargaDb.findById(idPenawaranHarga).get();
    }

    @Override
    public PenawaranHarga createPenawaranHarga(CreatePenawaranHargaRequestDTO createPenawaranHargaRequestDTO) {
        try {
            PenawaranHarga penawaranHarga = penawaranHargaMapper
                    .createPenawaranDTOToPenawaran(createPenawaranHargaRequestDTO);
            penawaranHarga = penawaranHargaDb.save(penawaranHarga);

            UUID idKlien = createPenawaranHargaRequestDTO.getIdKlien();
            if (idKlien != null) {
                UserModel klien = userRepository.findById(idKlien).orElse(null);
                if (klien != null && klien.getRole() == KLIEN) {
                    Klien klienConverted = modelMapper.map(klien, Klien.class);
                    penawaranHarga.setKlien(klienConverted);
                } else {
                    throw new NoSuchElementException("Id klien tidak valid");
                }
            }

            return penawaranHarga;
        } catch (Exception e) {
            throw new RuntimeException("Error creating Penawaran Harga", e);
        }
    }

    @Override
    public PenawaranHarga getPenawaranHargaByIdKlien(UUID klien) {
        return penawaranHargaDb.findByIdKlien(klien);
    }
}
