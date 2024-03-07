package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.sidha.api.service.PenawaranHargaService;
import com.sidha.api.model.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class PenawaranHargaController {
    @Autowired
    PenawaranHargaService penawaranHargaService;

    @GetMapping(value="/penawaran-harga/view-all")
    private List<PenawaranHarga> getAllPenawaranHarga(){
        return penawaranHargaService.getAllPenawaranHarga();
    }

    @GetMapping(value="/penawaran-harga/{idPenawaranHarga}")
    public PenawaranHarga getPenawaranHargaById(@PathVariable("idPenawaranHarga") String idPenawaranHarga){
        try{
            return penawaranHargaService.getPenawaranHargaById(UUID.fromString(idPenawaranHarga));
        }
        catch (NoSuchElementException e){
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Penawaran harga tidak ditemukan"
            );
        }
    }

    @GetMapping(value="/penawaran-harga/klien/{klien}/view-all")
    public PenawaranHarga getPenawaranHargaByIdKlien(@PathVariable("klien") String klien){
        return penawaranHargaService.getPenawaranHargaByIdKlien(UUID.fromString(klien));
    }

}
