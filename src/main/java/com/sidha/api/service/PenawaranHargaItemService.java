package com.sidha.api.service;

import com.sidha.api.model.PenawaranHargaItem;
import java.util.*;

public interface PenawaranHargaItemService {
    PenawaranHargaItem getPenawaranHargaItemById(UUID idPenawaranHargaItem);
    List<PenawaranHargaItem> getAllPenawaranHargaItemByIdPenawaranHarga(UUID idPenawaranHarga);
    List<PenawaranHargaItem> getAllPenawaranHargaItemBySource(String source);
    List<PenawaranHargaItem> getAllPenawaranHargaItemByIdKlien(UUID klien);
}
