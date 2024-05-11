package com.sidha.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.TawaranKerja;
import com.sidha.api.model.order.OrderItem;

@Repository
public interface TawaranKerjaDb extends JpaRepository<TawaranKerja, UUID> {
    List<TawaranKerja> findByOrderItem(OrderItem orderItem);

    List<TawaranKerja> findByIsDikonfirmasiKaryawanFalse();

    List<TawaranKerja> findBySopirId(UUID sopirId);

    List<TawaranKerja> findByOrderItemId(UUID orderItemId);

    List<TawaranKerja> findBySopirIdAndIsDikonfirmasiKaryawanTrue(UUID sopirId);

}

