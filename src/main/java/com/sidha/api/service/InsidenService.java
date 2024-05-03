package com.sidha.api.service;

import com.sidha.api.DTO.request.InsidenDTO;
import com.sidha.api.model.Insiden;
import com.sidha.api.model.Insiden.InsidenStatus;
import com.sidha.api.model.image.ImageData;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public interface InsidenService {
        public Insiden createInsiden(Insiden insiden, UUID sopirId, UUID orderItemId, MultipartFile buktiFoto)
                        throws IOException;

        public Insiden updateInsiden(UUID id, Insiden insidenDetails, UUID orderItemId, MultipartFile buktiFoto)
                        throws IOException;

        void deleteInsiden(UUID id);

        Insiden updateInsidenStatus(UUID id, InsidenStatus status);

        Insiden getInsidenById(UUID id);

        List<InsidenDTO> getAllInsidensWithSopirInfo();

        List<Insiden> getInsidensBySopirId(UUID sopirId);

        ImageData getBuktiFotoById(UUID insidenId);

        Long getTotalInsidenForToday();

        Long getTotalInsidenForThisWeek();

        Long getTotalInsidenForThisMonth();

        Long getTotalInsidenForThisYear();

        List<List<Object>> getTotalInsiden();

        List<List<Object>> getWeeklyTotalInsidenInMonth(int year, int month);

        List<List<Object>> getMonthlyTotalInsidenInYear(int year);

        List<List<Object>> getYearlyTotalInsidenInRange(int startYear, int endYear);
}
