package com.sidha.api.DTO.request.order;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderRequestDTO extends CreateOrderRequestDTO{
    private UUID id;
}
