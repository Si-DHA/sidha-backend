package com.sidha.api.DTO.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemConfirmRequestDTO {
    private String orderItemId;
    private Boolean isAccepted;
    private String rejectionReason;

    public void setIsAccepted(Object isAccepted) {
        if (isAccepted instanceof Boolean) {
            this.isAccepted = (Boolean) isAccepted;
        } else if (isAccepted instanceof String) {
            this.isAccepted = Boolean.parseBoolean((String) isAccepted);
        }
    }
}
