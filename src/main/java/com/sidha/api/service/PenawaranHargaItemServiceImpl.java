package com.sidha.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import com.sidha.api.model.PenawaranHargaItem;
import com.sidha.api.repository.PenawaranHargaItemDb;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PenawaranHargaItemServiceImpl implements PenawaranHargaItemService{
    @Autowired
    PenawaranHargaItemDb penawaranHargaItemDb;

    @Override
    public PenawaranHargaItem getPenawaranHargaItemById(UUID idPenawaranHargaItem){
        return penawaranHargaItemDb.findById(idPenawaranHargaItem).get();
    }

    @Override
    public List<PenawaranHargaItem> getAllPenawaranHargaItemByIdPenawaranHarga(UUID idPenawaranHarga){
        return penawaranHargaItemDb.findByIdPenawaranHarga(idPenawaranHarga);
    }
}
