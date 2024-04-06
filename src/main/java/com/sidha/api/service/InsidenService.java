package com.sidha.api.service;

import com.sidha.api.DTO.request.UpdateInsidenDTO;
import com.sidha.api.model.Insiden;

import jakarta.validation.Valid;

import java.io.IOException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface InsidenService {
    Insiden createInsiden(Insiden insiden, UUID sopirId, MultipartFile buktiFoto) throws IOException;
    Insiden updateInsiden(UUID id, @Valid UpdateInsidenDTO updateInsidenDTO, MultipartFile buktiFoto) throws IOException;
    void deleteInsiden(UUID id);
    Insiden getInsidenById(UUID id);
}
