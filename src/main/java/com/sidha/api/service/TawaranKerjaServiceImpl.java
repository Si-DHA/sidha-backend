package com.sidha.api.service;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.model.TawaranKerja;
import com.sidha.api.model.order.OrderItem;
import com.sidha.api.model.user.Sopir;
import com.sidha.api.repository.OrderItemDb;
import com.sidha.api.repository.TawaranKerjaDb;
import com.sidha.api.repository.UserDb;

@AllArgsConstructor
@Service
public class TawaranKerjaServiceImpl implements TawaranKerjaService {

    private TawaranKerjaDb tawaranKerjaDb;

    private UserDb userDb;

    private OrderItemDb orderItemDb;

    private OrderService orderService;

    private TrukService trukService;

    @Override
    public List<OrderItem> listAvailableOrderItems() {
        return orderItemDb.findByStatusOrder(0);
    }

    @Override
    public TawaranKerja acceptJobOffer(UUID sopirId, UUID orderItemId, String lokasi) {
        OrderItem orderItem = orderItemDb.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order Item not found"));

        Sopir sopir = (Sopir) userDb.findById(sopirId).orElseThrow(() -> new RuntimeException("Driver not found"));

        TawaranKerja tawaranKerja = new TawaranKerja();
        tawaranKerja.setOrderItem(orderItem);
        tawaranKerja.setSopir(sopir);
        tawaranKerja.setLokasi(lokasi);
        tawaranKerja.setIsDikonfirmasiKaryawan(false);
        
        return tawaranKerjaDb.save(tawaranKerja);
    }

    @Override
    public void confirmJobOffer(UUID karyawanId, UUID tawaranKerjaId) {
        TawaranKerja confirmedTawaranKerja = tawaranKerjaDb.findById(tawaranKerjaId)
                .orElseThrow(() -> new IllegalArgumentException("Tawaran Kerja not found"));
        OrderItem orderItem = confirmedTawaranKerja.getOrderItem();
        
        // Set all related TawaranKerja to not confirmed
        tawaranKerjaDb.findByOrderItem(orderItem).forEach(tk -> {
            tk.setIsDikonfirmasiKaryawan(false);
            tawaranKerjaDb.save(tk);
        });

        // Confirm the selected TawaranKerja
        confirmedTawaranKerja.setIsDikonfirmasiKaryawan(true);
        tawaranKerjaDb.save(confirmedTawaranKerja);

        // Update the status of the OrderItem
        var sopir = confirmedTawaranKerja.getSopir();
        orderItem.setSopir(sopir);
        orderItem.setStatusOrder(1); // Status indicating that the job offer has been confirmed
        orderItemDb.save(orderItem);

        var orderItemHistories = orderItem.getOrderItemHistories();

        String descriptionHistoryConfirm = "Mengonfirmasi order item " + orderItem.getId();
        orderItemHistories.add(
                orderService.addOrderItemHistory(orderItem, descriptionHistoryConfirm, "PT DHA")
        );

        var trukSopir = trukService.findTrukByIdSopir(sopir.getId());
        String descriptionHistoryAssign;
        if (trukSopir != null) {
            descriptionHistoryAssign = "Menugaskan sopir " + sopir.getName() + " - " + trukSopir.getLicensePlate();
        } else {
            throw new RuntimeException("Sopir tidak memiliki truk");
        }
        orderItemHistories.add(
                orderService.addOrderItemHistory(orderItem, descriptionHistoryAssign, "PT DHA")
        );

    }

    @Override
    public List<TawaranKerja> getAllJobOffers() {
        return tawaranKerjaDb.findAll();
    }

    @Override
    public List<TawaranKerja> getJobOffersAcceptedBySopir(UUID sopirId) {
        return tawaranKerjaDb.findBySopirId(sopirId);
    }

    @Override
    public List<TawaranKerja> getTawaranKerjaByOrderItemId(UUID orderItemId) {
        return tawaranKerjaDb.findByOrderItemId(orderItemId);
    }
}
