package com.sidha.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import com.sidha.api.model.PenawaranHarga;
import com.sidha.api.repository.PenawaranHargaDb;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PenawaranHargaServiceImpl implements PenawaranHargaService{
    @Autowired
    PenawaranHargaDb penawaranHargaDb;

    @Override
    public List<PenawaranHarga> getAllPenawaranHarga(){
        return penawaranHargaDb.findAll();
    }

    @Override
    public PenawaranHarga getPenawaranHargaById(UUID idPenawaranHarga){
        return penawaranHargaDb.findById(idPenawaranHarga).get();
    }
}
