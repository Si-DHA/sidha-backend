package com.sidha.api.service;

import com.sidha.api.model.Insiden;
import com.sidha.api.model.Insiden.InsidenStatus;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.user.Sopir;
import com.sidha.api.repository.InsidenRepository;
import com.sidha.api.repository.UserDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InsidenServiceImpl implements InsidenService {

    @Autowired
    private InsidenRepository insidenRepository;

    @Autowired
    private UserDb userDb;
    
    @Autowired
    private StorageService storageService;

    @Override
    public Insiden createInsiden(Insiden insiden, UUID sopirId, MultipartFile buktiFoto) throws IOException {
        Sopir sopir = (Sopir) userDb.findById(sopirId).orElseThrow(() -> new RuntimeException("Driver not found"));

        if (buktiFoto != null && !buktiFoto.isEmpty()) {
            String uniqueIdentifier = sopirId + "_" + System.currentTimeMillis() + "_" + buktiFoto.getOriginalFilename();
            ImageData imageData = storageService.uploadImageAndSaveToDB(buktiFoto, uniqueIdentifier);
            insiden.setBuktiFoto(imageData);
        }
        
        insiden.setSopir(sopir);
        return insidenRepository.save(insiden);
    }

    @Override
    public Insiden updateInsiden(UUID id, Insiden insidenDetails, MultipartFile buktiFoto) throws IOException {
        Insiden existingInsiden = insidenRepository.findById(id)
                                    .orElseThrow(() -> new RuntimeException("Insiden not found"));

        if (existingInsiden.getStatus() != InsidenStatus.PENDING) {
            throw new RuntimeException("Insiden cannot be edited as it is not in PENDING status.");
        }

        if (buktiFoto != null && !buktiFoto.isEmpty()) {
            if (existingInsiden.getBuktiFoto() != null) {
                storageService.deleteImageFile(existingInsiden.getBuktiFoto());
            }

            String uniqueIdentifier = existingInsiden.getSopir().getId() + "_" + System.currentTimeMillis() + "_" + buktiFoto.getOriginalFilename();
            ImageData newImageData = storageService.uploadImageAndSaveToDB(buktiFoto, uniqueIdentifier);
            existingInsiden.setBuktiFoto(newImageData);
        }

        existingInsiden.setKategori(insidenDetails.getKategori());
        existingInsiden.setLokasi(insidenDetails.getLokasi());
        existingInsiden.setKeterangan(insidenDetails.getKeterangan());
        existingInsiden.setUpdatedAt(LocalDateTime.now());
        return insidenRepository.save(existingInsiden);
    }

    @Override
    public void deleteInsiden(UUID id) {
        Insiden insiden = insidenRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Insiden not found"));

        if (insiden.getStatus() != InsidenStatus.PENDING) {
            throw new RuntimeException("Insiden cannot be deleted as it is not in PENDING status.");
        }

        insiden.setDeleted(true);
        insidenRepository.save(insiden);
    }

    @Override
    public Insiden updateInsidenStatus(UUID id, InsidenStatus status) {
        Insiden insiden = insidenRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Insiden not found"));
        insiden.setStatus(status);
        insiden.setUpdatedAt(LocalDateTime.now());
        return insidenRepository.save(insiden);
    }

    @Override
    public Insiden getInsidenById(UUID id) {
        return insidenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insiden not found"));
    }

    @Override
    public List<Insiden> getAllInsidens() {
        return insidenRepository.findAllByIsDeletedFalse(); // Ensure this method exists to filter out soft-deleted records
    }
}
