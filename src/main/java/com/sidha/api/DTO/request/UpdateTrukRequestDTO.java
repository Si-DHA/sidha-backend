package com.sidha.api.DTO.request;

import com.sidha.api.model.enumerator.TipeTruk;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrukRequestDTO extends CreateTrukRequestDTO {
    private UUID idTruk;
}
