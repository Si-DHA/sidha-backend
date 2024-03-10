package com.sidha.api.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrukRequestDTO extends CreateTrukRequestDTO {

    @NotNull(message = "ID truk tidak boleh kosong")

    private UUID idTruk;
}
