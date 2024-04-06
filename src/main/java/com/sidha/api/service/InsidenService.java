package com.sidha.api.service;

import com.sidha.api.model.Insiden;
import com.sidha.api.model.Insiden.InsidenStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface InsidenService {
    Insiden createInsiden(Insiden insiden, UUID sopirId, MultipartFile buktiFoto) throws IOException;
    Insiden updateInsiden(UUID id, Insiden insidenDetails, MultipartFile buktiFoto) throws IOException;
    void deleteInsiden(UUID id);
    Insiden updateInsidenStatus(UUID id, InsidenStatus status);
    Insiden getInsidenById(UUID id);
    List<Insiden> getAllInsidens();
}
