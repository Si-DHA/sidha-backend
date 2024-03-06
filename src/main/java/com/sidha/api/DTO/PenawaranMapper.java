package com.sidha.api.DTO;

import org.mapstruct.Mapper;

import com.sidha.api.DTO.request.CreatePenawaranDTO;
import com.sidha.api.model.Penawaran;

@Mapper(componentModel = "spring")
public interface PenawaranMapper {

    Penawaran createPenawaranDTOToPenawaran(CreatePenawaranDTO createPenawaranDTO);
    
}
