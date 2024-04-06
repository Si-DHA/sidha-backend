package com.sidha.api.service;

import com.sidha.api.DTO.PenawaranHargaItemMapper;
import com.sidha.api.DTO.request.CreatePenawaranHargaItemRequestDTO;
import com.sidha.api.DTO.request.UpdatePenawaranHargaItemRequestDTO;
import com.sidha.api.model.user.Klien;
import com.sidha.api.model.PenawaranHarga;
import com.sidha.api.model.UserModel;
import com.sidha.api.repository.PenawaranHargaDb;
import com.sidha.api.repository.UserDb;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.*;
import com.sidha.api.model.PenawaranHargaItem;
import com.sidha.api.repository.PenawaranHargaItemDb;
import jakarta.transaction.Transactional;

import static com.sidha.api.model.enumerator.Role.KLIEN;

@Service
@Transactional
@AllArgsConstructor
public class PenawaranHargaItemServiceImpl implements PenawaranHargaItemService{

    private PenawaranHargaItemDb penawaranHargaItemDb;
    private PenawaranHargaDb penawaranHargaDb;

    private UserDb userRepository;
    private PenawaranHargaItemMapper penawaranHargaItemMapper;
    private ModelMapper modelMapper;

    @Override
    public PenawaranHargaItem getPenawaranHargaItemById(UUID idPenawaranHargaItem){
        return penawaranHargaItemDb.findById(idPenawaranHargaItem).orElse(null);
    }

    @Override
    public List<PenawaranHargaItem> getAllPenawaranHargaItemByIdPenawaranHarga(UUID idPenawaranHarga){
        return penawaranHargaItemDb.findByIdPenawaranHarga(idPenawaranHarga);
    }

    @Override
    public List<PenawaranHargaItem> getAllPenawaranHargaItemBySource(String source){
        return penawaranHargaItemDb.findBySource(source);
    }

    @Override
    public List<PenawaranHargaItem> getAllPenawaranHargaItemByIdKlien(UUID klien){
        return penawaranHargaItemDb.findByIdKlien(klien);
    }

    @Override
    public PenawaranHargaItem createPenawaranHargaItem(
            @Valid CreatePenawaranHargaItemRequestDTO createPenawaranHargaItemRequestDTO) {
        try {
            PenawaranHargaItem penawaranHargaItem = penawaranHargaItemMapper
                    .createPenawaranHargaItemDTOToEntity(createPenawaranHargaItemRequestDTO);
            penawaranHargaItem = penawaranHargaItemDb.save(penawaranHargaItem);

            UUID idKlien = createPenawaranHargaItemRequestDTO.getIdKlien();
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

            UUID idPenawaranHarga = createPenawaranHargaItemRequestDTO.getIdPenawaranHarga();
            if (idPenawaranHarga != null) {
                PenawaranHarga penawaranHarga = penawaranHargaDb.findById(idPenawaranHarga).orElse(null);
                if (penawaranHarga != null) {
                    penawaranHargaItem.setPenawaranHarga(penawaranHarga);
                    penawaranHarga.getListPenawaranHargaItem().add(penawaranHargaItem);
                    penawaranHargaDb.save(penawaranHarga);
                } else {
                    throw new NoSuchElementException("Id penawaran harga tidak valid");
                }
            }

            return penawaranHargaItem;
        } catch (Exception e) {
            throw new RuntimeException("Error creating Penawaran Harga", e);
        }
    }

    @Override
    public PenawaranHargaItem updatePenawaranHargaItem(
            @Valid UpdatePenawaranHargaItemRequestDTO updatePenawaranHargaItemRequestDTO) {
        UUID idPenawaranHargaItem = updatePenawaranHargaItemRequestDTO.getIdPenawaranHargaItem();
        PenawaranHargaItem existingItem = penawaranHargaItemDb.findById(idPenawaranHargaItem).orElse(null);

        if (existingItem != null) {
            existingItem.setCddPrice(updatePenawaranHargaItemRequestDTO.getCddPrice());
            existingItem.setCddLongPrice(updatePenawaranHargaItemRequestDTO.getCddLongPrice());
            existingItem.setWingboxPrice(updatePenawaranHargaItemRequestDTO.getWingboxPrice());
            existingItem.setFusoPrice(updatePenawaranHargaItemRequestDTO.getFusoPrice());

            return penawaranHargaItemDb.save(existingItem);
        } else {
            throw new NoSuchElementException("Penawaran Harga Item with id " + idPenawaranHargaItem + " not found");
        }
    }

    @Override
    public void deletePenawaranHargaItem(UUID idPenawaranHargaItem) {
        PenawaranHargaItem existingItem = penawaranHargaItemDb.findById(idPenawaranHargaItem).orElse(null);

        if (existingItem != null) {
            existingItem.setIsDeleted(true);
            penawaranHargaItemDb.save(existingItem);
        } else {
            throw new NoSuchElementException("PenawaranHargaItem with id " + idPenawaranHargaItem + " not found");
        }
    }
}
