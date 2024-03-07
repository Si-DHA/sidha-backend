package com.sidha.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import com.sidha.api.DTO.PenawaranHargaMapper;
import com.sidha.api.DTO.request.CreatePenawaranHargaItemRequestDTO;
import com.sidha.api.DTO.request.CreatePenawaranHargaRequestDTO;
import com.sidha.api.model.Klien;
import com.sidha.api.model.PenawaranHarga;
import com.sidha.api.model.PenawaranHargaItem;
import com.sidha.api.model.UserModel;
import com.sidha.api.repository.PenawaranHargaDb;
import com.sidha.api.repository.UserDb;
import static com.sidha.api.model.enumerator.Role.KLIEN;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PenawaranHargaServiceImpl implements PenawaranHargaService {

    @Autowired
    PenawaranHargaDb penawaranHargaDb;
    private UserDb userRepository;
    private ModelMapper modelMapper;
    private PenawaranHargaItemService penawaranHargaItemService;
    private PenawaranHargaMapper penawaranHargaMapper;

    @Override
    public List<PenawaranHarga> getAllPenawaranHarga(){
        return penawaranHargaDb.findAll();
    }

    @Override
    public PenawaranHarga getPenawaranHargaById(UUID idPenawaranHarga){
        return penawaranHargaDb.findById(idPenawaranHarga).get();
    }

    @Override
    public PenawaranHarga createPenawaranHarga(CreatePenawaranHargaRequestDTO createPenawaranHargaRequestDTO) {
        try {
            PenawaranHarga penawaranHarga = new PenawaranHarga();
            
            Klien idKlien = createPenawaranHargaRequestDTO.getKlien();
            if (idKlien != null) {
                UserModel klien = userRepository.findById(idKlien).orElse(null);
                if (klien != null && klien.getRole() == KLIEN) {
                    Klien klienConverted = modelMapper.map(klien, Klien.class);
                    penawaranHarga.setKlien(klienConverted);
                } else {
                    throw new NoSuchElementException("Id klien tidak valid");
                }
            }

            List<PenawaranHargaItem> listPenawaranHargaItem = new ArrayList<>();
            for (CreatePenawaranHargaItemRequestDTO itemDTO : createPenawaranHargaRequestDTO.getListPenawaranHargaItem()) {
                PenawaranHargaItem penawaranHargaItem = penawaranHargaItemService.createPenawaranHargaItem(itemDTO);
                listPenawaranHargaItem.add(penawaranHargaItem);
            }

            penawaranHarga.setListPenawaranHargaItem(listPenawaranHargaItem);
            penawaranHarga.setPenawaranHargaCreatedAt(createPenawaranHargaRequestDTO.getPenawaranHargaCreatedAt());
            penawaranHarga.setPenawaranHargaUpdatedAt(createPenawaranHargaRequestDTO.getPenawaranHargaUpdatedAt());

            return penawaranHargaDb.save(penawaranHarga);
        } catch (Exception e) {
            throw new RuntimeException("Error creating Penawaran Harga", e);
        }
    }
}
