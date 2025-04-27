package gpl.karina.profile.restdto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class AddPurchaseIdDTO {
    private String purchaseId;
    private UUID supplierId;
}
