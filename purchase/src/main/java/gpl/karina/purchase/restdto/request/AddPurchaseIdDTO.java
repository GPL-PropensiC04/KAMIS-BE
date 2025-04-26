package gpl.karina.purchase.restdto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class AddPurchaseIdDTO {
    private String purchaseId;
    private UUID supplierId;
}
