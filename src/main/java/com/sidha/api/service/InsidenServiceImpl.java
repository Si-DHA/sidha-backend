package com.sidha.api.service;

import com.sidha.api.DTO.request.InsidenDTO;
import com.sidha.api.model.Insiden;
import com.sidha.api.model.Insiden.InsidenStatus;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.model.user.Sopir;
import com.sidha.api.repository.InsidenRepository;
import com.sidha.api.repository.UserDb;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.time.LocalTime;

@Service
@Transactional
@AllArgsConstructor
public class InsidenServiceImpl implements InsidenService {

    private InsidenRepository insidenRepository;

    private UserDb userDb;

    @Autowired
    private StorageService storageService;

    private OrderService orderService;

    @Override
    public Insiden createInsiden(Insiden insiden, UUID sopirId, String orderItemId, MultipartFile buktiFoto)
            throws IOException {
        Sopir sopir = (Sopir) userDb.findById(sopirId).orElseThrow(() -> new RuntimeException("Driver not found"));

        if (buktiFoto != null && !buktiFoto.isEmpty()) {
            String uniqueIdentifier = sopirId + "_" + System.currentTimeMillis() + "_"
                    + buktiFoto.getOriginalFilename();
            ImageData imageData = storageService.uploadImageAndSaveToDB(buktiFoto, uniqueIdentifier);
            insiden.setBuktiFoto(imageData);
        }

        insiden.setSopir(sopir);

        OrderItem orderItem = insiden.getOrderItem();
        var orderItemHistories = orderItem.getOrderItemHistories();
        String descriptionHistory = "Membuat laporan insiden (" + insiden.getKategori() + ")";
        String sopirName = "Sopir " + sopir.getName();
        orderItemHistories.add(
                orderService.addOrderItemHistory(orderItem, descriptionHistory, sopirName)
        );

        return insidenRepository.save(insiden);
    }

    @Override
    public Insiden updateInsiden(UUID id, Insiden insidenDetails, String orderItemId, MultipartFile buktiFoto)
            throws IOException {
        Insiden existingInsiden = insidenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insiden not found"));

        if (existingInsiden.getStatus() != InsidenStatus.PENDING) {
            throw new RuntimeException("Insiden cannot be edited as it is not in PENDING status.");
        }

        if (buktiFoto != null && !buktiFoto.isEmpty()) {
            if (existingInsiden.getBuktiFoto() != null) {
                storageService.deleteImageFile(existingInsiden.getBuktiFoto());
            }

            String uniqueIdentifier = existingInsiden.getSopir().getId() + "_" + System.currentTimeMillis() + "_"
                    + buktiFoto.getOriginalFilename();
            ImageData newImageData = storageService.uploadImageAndSaveToDB(buktiFoto, uniqueIdentifier);
            existingInsiden.setBuktiFoto(newImageData);
        }

        existingInsiden.setKategori(insidenDetails.getKategori());
        existingInsiden.setLokasi(insidenDetails.getLokasi());
        existingInsiden.setKeterangan(insidenDetails.getKeterangan());
        existingInsiden.setUpdatedAt(LocalDateTime.now());

        // add order item history
        var orderItem = existingInsiden.getOrderItem();
        var orderItemHistories = orderItem.getOrderItemHistories();
        String sopir = "Sopir " + orderItem.getSopir().getName();
        String descriptionHistory = "Mengubah laporan insiden (" + existingInsiden.getKategori() + ")";
        orderItemHistories.add(
                orderService.addOrderItemHistory(orderItem, descriptionHistory, sopir)
        );

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

        var orderItem = insiden.getOrderItem();
        var orderItemHistories = orderItem.getOrderItemHistories();

        if (status == InsidenStatus.ON_PROGRESS) {
            String descriptionHistory = "Memproses laporan insiden (" + insiden.getKategori() + ")";
            orderItemHistories.add(
                    orderService.addOrderItemHistory(orderItem, descriptionHistory, "PT DHA")
            );
        } else if (status == InsidenStatus.COMPLETED) {
            String descriptionHistory = "Menyelesaikan laporan insiden (" + insiden.getKategori() + ")";
            orderItemHistories.add(
                    orderService.addOrderItemHistory(orderItem, descriptionHistory, "PT DHA")
            );
        } else if (status == InsidenStatus.CANCELLED) {
            String descriptionHistory = "Membatalkan order item karena insiden (" + insiden.getKategori() + ")";
            orderItem.setStatusOrder(-1);

            var order = orderService.getOrderByOrderItem(orderItem.getId());
            var invoice = order.getInvoice();
            Long orderPrice = order.getTotalPrice() - orderItem.getPrice();
            Long dp = (long) (orderPrice * 0.6);
            Long pelunasan = (long) (orderPrice * 0.4);
            order.setTotalPrice(orderPrice);
            invoice.setTotalDp(BigDecimal.valueOf(dp));
            invoice.setTotalPelunasan(BigDecimal.valueOf(pelunasan));

            orderItemHistories.add(
                    orderService.addOrderItemHistory(orderItem, descriptionHistory, "PT DHA")
            );
        }

        return insidenRepository.save(insiden);
    }

    public List<InsidenDTO> getAllInsidensWithSopirInfo() {
        List<Insiden> allInsidens = insidenRepository.findAll();
        return allInsidens.stream().map(insiden -> {
            InsidenDTO dto = new InsidenDTO();
            dto.setId(insiden.getId());
            dto.setKategori(insiden.getKategori());
            dto.setLokasi(insiden.getLokasi());
            dto.setKeterangan(insiden.getKeterangan());
            dto.setSopirId(insiden.getSopir().getId());
            dto.setSopirName(insiden.getSopir().getName());
            dto.setBuktiFoto(insiden.getBuktiFoto());
            dto.setCreatedAt(insiden.getCreatedAt());
            dto.setUpdatedAt(insiden.getUpdatedAt());
            dto.setStatus(insiden.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<Insiden> getInsidensBySopirId(UUID sopirId) {
        Sopir sopir = (Sopir) userDb.findById(sopirId).orElseThrow(() -> new RuntimeException("Driver not found"));
        return insidenRepository.findBySopir(sopir);
    }

    public ImageData getBuktiFotoById(UUID insidenId) {
        Insiden insiden = insidenRepository.findById(insidenId)
                .orElseThrow(() -> new RuntimeException("Insiden not found"));
        return insiden.getBuktiFoto();
    }

    @Override
    public Insiden getInsidenById(UUID id) {
        return insidenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insiden not found"));
    }

    @Override
    public Long getTotalInsidenForToday() {
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endDateTime = startDateTime.plusDays(1);
        return insidenRepository.getTotalInsidenForToday(startDateTime, endDateTime);
    }

    @Override
    public Long getTotalInsidenForThisWeek() {
        return insidenRepository.getTotalInsidenForThisWeek();
    }

    @Override
    public Long getTotalInsidenForThisMonth() {
        return insidenRepository.getTotalInsidenForThisMonth();
    }

    @Override
    public Long getTotalInsidenForThisYear() {
        return insidenRepository.getTotalInsidenForThisYear();
    }

    @Override
    public List<List<Object>> getTotalInsiden() {
        Long today = getTotalInsidenForToday();
        Long weekly = getTotalInsidenForThisWeek();
        Long monthly = getTotalInsidenForThisMonth();
        Long yearly = getTotalInsidenForThisYear();
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("today", today));
        result.add(Arrays.asList("weekly", weekly));
        result.add(Arrays.asList("monthly", monthly));
        result.add(Arrays.asList("yearly", yearly));
        return result;
    }

    private List<Integer> generateAllWeeksInMonth(int year, int month) {
        List<Integer> weeks = new ArrayList<>();
        LocalDate date = LocalDate.of(year, month, 1);
        while (date.getMonthValue() == month) {
            int week = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            if (!weeks.contains(week)) {
                weeks.add(week);
            }
            date = date.plusDays(1);
        }
        return weeks;
    }

    @Override
    public List<List<Object>> getWeeklyTotalInsidenInMonth(int year, int month) {
        List<Integer> allWeeks = generateAllWeeksInMonth(year, month);
        List<Object[]> data = insidenRepository.getWeeklyTotalInsidenInMonth(year, month);
        Map<Integer, Object[]> dataMap = new HashMap<>();
        for (Object[] row : data) {
            int week = ((Number) row[0]).intValue();
            dataMap.put(week, row);
        }

        int firstWeek = allWeeks.get(0);

        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Minggu", "Jumlah Insiden"));
        for (int week : allWeeks) {
            List<Object> row = new ArrayList<>();
            if (dataMap.containsKey(week)) {
                Object[] rowValue = dataMap.get(week);
                row.add(((Number) rowValue[0]).intValue() - firstWeek + 1);
                row.add(rowValue[1]);
            } else {
                row.add(week - firstWeek + 1);
                row.add(0);
            }
            result.add(row);
        }
        return result;
    }

    @Override
    public List<List<Object>> getMonthlyTotalInsidenInYear(int year) {
        List<Object[]> data = insidenRepository.getMonthlyTotalInsidenInYear(year);
        Map<Integer, Object[]> dataMap = new HashMap<>();
        for (Object[] row : data) {
            int month = ((Number) row[0]).intValue();
            dataMap.put(month, row);
        }

        String[] monthNames = new String[] { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus",
                "September", "Oktober", "November", "Desember" };

        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Bulan", "Jumlah Insiden"));

        for (int month = 1; month <= 12; month++) {
            List<Object> row = new ArrayList<>();
            if (dataMap.containsKey(month)) {
                Object[] rowValue = dataMap.get(month);
                row.add(monthNames[month - 1]);
                row.add(rowValue[1]);
            } else {
                row.add(monthNames[month - 1]);
                row.add(0);
            }
            result.add(row);
        }

        return result;
    }

    @Override
    public List<List<Object>> getYearlyTotalInsidenInRange(int startYear, int endYear) {
        List<Object[]> clientData = insidenRepository.getYearlyTotalInsidenInRange(startYear, endYear);

        Map<Integer, Object[]> clientDataMap = new HashMap<>();
        for (Object[] data : clientData) {
            int year = ((Number) data[0]).intValue();
            clientDataMap.put(year, data);
        }

        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Tahun", "Jumlah Insiden"));
        for (int year = startYear; year <= endYear; year++) {
            List<Object> row = new ArrayList<>();
            row.add(year);
            if (clientDataMap.containsKey(year)) {
                row.add(clientDataMap.get(year)[1]);
            } else {
                row.add(0);
            }
            result.add(row);
        }

        return result;
    }

    @Override
    public List<Insiden> getListInsidenForToday() {
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endDateTime = startDateTime.plusDays(1);
        return insidenRepository.getListInsidenForToday(startDateTime, endDateTime);
    }

    @Override
    public List<Insiden> getListInsidenForThisWeek() {
        return insidenRepository.getListInsidenForThisWeek();
    }

    @Override
    public List<Insiden> getListInsidenForThisMonth() {
        return insidenRepository.getListInsidenForThisMonth();
    }   

    @Override
    public List<Insiden> getListInsidenForThisYear() {
        return insidenRepository.getListInsidenForThisYear();
    }

}
