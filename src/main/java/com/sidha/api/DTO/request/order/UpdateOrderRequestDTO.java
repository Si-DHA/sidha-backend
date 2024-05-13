package com.sidha.api.DTO.request.order;

import java.util.UUID;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderRequestDTO {
    private String id;

    private List<UpdateOrderItemRequestDTO> orderItems;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date tanggalPengiriman;

    private UUID klienId;
}
