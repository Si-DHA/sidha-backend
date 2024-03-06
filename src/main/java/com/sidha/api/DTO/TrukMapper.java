package com.sidha.api.DTO;

import com.sidha.api.DTO.request.CreateTrukRequestDTO;
import com.sidha.api.model.Truk;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrukMapper {

    Truk createTrukRequestDTOToTruk(CreateTrukRequestDTO createTrukRequestDTO);

}
