package com.sidha.api.service;

import com.sidha.api.DTO.request.UpdateInsidenDTO;
import com.sidha.api.model.ImageData;
import com.sidha.api.model.Insiden;
import com.sidha.api.model.Sopir;
import com.sidha.api.repository.InsidenRepository;
import com.sidha.api.service.InsidenService;
import com.sidha.api.service.StorageService;
import com.sidha.api.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class InsidenServiceImpl implements InsidenService {

    @Autowired
    private InsidenRepository insidenRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private StorageService storageService;

    @Override
    public Insiden createInsiden(Insiden insiden, UUID sopirId, MultipartFile buktiFoto) throws IOException {
        Sopir sopir = (Sopir) userService.findById(sopirId);
        insiden.setSopir(sopir);
        if (buktiFoto != null && !buktiFoto.isEmpty()) {
            ImageData imageData = storageService.uploadImageAndSaveToDB(buktiFoto, sopir);
            insiden.setBuktiFoto(imageData);
        }
        return insidenRepository.save(insiden);
    }

    @Override
    public Insiden updateInsiden(UUID id, @Valid UpdateInsidenDTO updateInsidenDTO, MultipartFile buktiFoto)
            throws IOException {
        Insiden existingInsiden = insidenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Insiden not found with ID: " + id));

        existingInsiden.setKategori(updateInsidenDTO.getKategori());
        existingInsiden.setLokasi(updateInsidenDTO.getLokasi());
        existingInsiden.setKeterangan(updateInsidenDTO.getKeterangan());

        if (buktiFoto != null && !buktiFoto.isEmpty()) {
            ImageData imageData = storageService.uploadImageAndSaveToDB(buktiFoto, existingInsiden.getSopir());
            existingInsiden.setBuktiFoto(imageData);
        }
        return insidenRepository.save(existingInsiden);
    }

    @Override
    public void deleteInsiden(UUID id) {
        Insiden insiden = getInsidenById(id);
        insidenRepository.delete(insiden);
    }

    @Override
    public Insiden getInsidenById(UUID id) {
        return insidenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Insiden not found with ID: " + id));
    }

}
