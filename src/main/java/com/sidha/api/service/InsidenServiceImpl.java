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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class InsidenServiceImpl implements InsidenService {

    private InsidenRepository insidenRepository;

    private UserDb userDb;

    private StorageService storageService;

    private OrderService orderService;

    @Override
    public Insiden createInsiden(Insiden insiden, UUID sopirId, UUID orderItemId, MultipartFile buktiFoto) throws IOException {
        Sopir sopir = (Sopir) userDb.findById(sopirId).orElseThrow(() -> new RuntimeException("Driver not found"));

        if (buktiFoto != null && !buktiFoto.isEmpty()) {
            String uniqueIdentifier = sopirId + "_" + System.currentTimeMillis() + "_" + buktiFoto.getOriginalFilename();
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
    public Insiden updateInsiden(UUID id, Insiden insidenDetails, UUID orderItemId, MultipartFile buktiFoto) throws IOException {
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
    
}
