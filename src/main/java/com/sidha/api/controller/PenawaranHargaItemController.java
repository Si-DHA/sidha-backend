package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.sidha.api.service.PenawaranHargaItemService;
import com.sidha.api.model.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class PenawaranHargaItemController {
    @Autowired
    PenawaranHargaItemService penawaranHargaItemService;

    @GetMapping(value="/penawaran-harga-item/{idPenawaranHargaItem}")
    public PenawaranHargaItem getPenawaranHargaItemById(@PathVariable("idPenawaranHargaItem") String idPenawaranHargaItem){
        try{
            return penawaranHargaItemService.getPenawaranHargaItemById(UUID.fromString(idPenawaranHargaItem));
        }
        catch (NoSuchElementException e){
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Penawaran harga item tidak ditemukan"
            );
        }
    }

    @GetMapping(value="/penawaran-harga-item/{idPenawaranHarga}/view-all")
    public List<PenawaranHargaItem> getAllPenawaranHargaItemByIdPenawaranHarga(@PathVariable("idPenawaranHarga") UUID idPenawaranHarga){
        List<PenawaranHargaItem> listPenawaranHargaItem = penawaranHargaItemService.getAllPenawaranHargaItemByIdPenawaranHarga(idPenawaranHarga);
        return listPenawaranHargaItem;
    }

    @GetMapping(value="/penawaran-harga-item/source/{source}/view-all")
    public List<PenawaranHargaItem> getAllPenawaranHargaItemBySource(@PathVariable("source") String source){
        List<PenawaranHargaItem> listPenawaranHargaItem = penawaranHargaItemService.getAllPenawaranHargaItemBySource(source);
        return listPenawaranHargaItem;
    }

    @GetMapping(value="/penawaran-harga-item/klien/{klien}/view-all")
    public List<PenawaranHargaItem> getAllPenawaranHargaItemByIdKlien(@PathVariable("klien") String klien){
        List<PenawaranHargaItem> listPenawaranHargaItem = penawaranHargaItemService.getAllPenawaranHargaItemByIdKlien(UUID.fromString(klien));
        return listPenawaranHargaItem;
    }
}
