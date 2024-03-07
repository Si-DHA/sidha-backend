package com.sidha.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import com.sidha.api.DTO.PenawaranHargaItemMapper;
import com.sidha.api.DTO.request.CreatePenawaranHargaItemRequestDTO;
import com.sidha.api.model.Klien;
import com.sidha.api.model.PenawaranHargaItem;
import com.sidha.api.model.UserModel;
import com.sidha.api.repository.PenawaranHargaItemDb;
import com.sidha.api.repository.UserDb;
import static com.sidha.api.model.enumerator.Role.KLIEN;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Transactional
public class PenawaranHargaItemServiceImpl implements PenawaranHargaItemService {
    @Autowired
    private UserDb userRepository;

    @Autowired
    private PenawaranHargaItemMapper penawaranHargaItemMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PenawaranHargaItemDb penawaranHargaItemDb;

    @Override
    public PenawaranHargaItem getPenawaranHargaItemById(UUID idPenawaranHargaItem) {
        return penawaranHargaItemDb.findById(idPenawaranHargaItem).orElse(null);
    }

    @Override
    public List<PenawaranHargaItem> getAllPenawaranHargaItemByIdPenawaranHarga(UUID idPenawaranHarga) {
        return penawaranHargaItemDb.findByIdPenawaranHarga(idPenawaranHarga);
    }

    @Override
    public PenawaranHargaItem createPenawaranHargaItem(
            @Valid CreatePenawaranHargaItemRequestDTO createPenawaranHargaItemRequestDTO) {
        PenawaranHargaItem penawaranHargaItem = new PenawaranHargaItem();

        penawaranHargaItem.setSource(createPenawaranHargaItemRequestDTO.getSource());
        penawaranHargaItem.setDestination(createPenawaranHargaItemRequestDTO.getDestination());
        penawaranHargaItem.setCddPrice(createPenawaranHargaItemRequestDTO.getCddPrice());
        penawaranHargaItem.setCddLongPrice(createPenawaranHargaItemRequestDTO.getCddLongPrice());
        penawaranHargaItem.setWingboxPrice(createPenawaranHargaItemRequestDTO.getWingboxPrice());
        penawaranHargaItem.setFusoPrice(createPenawaranHargaItemRequestDTO.getFusoPrice());

        var idKlien = createPenawaranHargaItemRequestDTO.getIdKlien();
        if (idKlien != null) {
            UserModel klien = userRepository.findById(idKlien).orElse(null);
            if (klien != null && klien.getRole() == KLIEN) {
                Klien klienConverted = modelMapper.map(klien, Klien.class);
                penawaranHargaItem.setKlien(klienConverted);
                
                klienConverted.getListPenawaranHargaItem().add(penawaranHargaItem);
            } else {
                throw new NoSuchElementException("Id klien tidak valid");
            }
        }

        // Save to the database
        return penawaranHargaItemDb.save(penawaranHargaItem);
    }
}
