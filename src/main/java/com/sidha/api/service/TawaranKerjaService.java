package com.sidha.api.service;

import java.util.List;
import java.util.UUID;

import com.sidha.api.model.TawaranKerja;
import com.sidha.api.model.order.OrderItem;

public interface TawaranKerjaService {
    
    List<OrderItem> listAvailableOrderItems();

    TawaranKerja acceptJobOffer(UUID sopirId, UUID orderItemId, String lokasi);
    
    void confirmJobOffer(UUID karyawanId, UUID tawaranKerjaId);  
}
