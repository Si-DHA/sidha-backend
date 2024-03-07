package com.sidha.api.DTO;

import org.mapstruct.Mapper;
import com.sidha.api.DTO.request.CreatePenawaranHargaItemRequestDTO;
import com.sidha.api.model.PenawaranHargaItem;

@Mapper(componentModel = "spring")
public interface PenawaranHargaItemMapper {
    PenawaranHargaItem createPenawaranHargaItemDTOToEntity(CreatePenawaranHargaItemRequestDTO createPenawaranHargaItemDTO);
}
