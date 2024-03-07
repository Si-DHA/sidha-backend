package com.sidha.api.DTO;

import org.mapstruct.Mapper;
import com.sidha.api.DTO.request.CreatePenawaranHargaRequestDTO;
import com.sidha.api.model.PenawaranHarga;

@Mapper(componentModel = "spring")
public interface PenawaranHargaMapper {

    PenawaranHarga createPenawaranDTOToPenawaran(CreatePenawaranHargaRequestDTO createPenawaranDTO);
}
