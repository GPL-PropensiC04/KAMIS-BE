package gpl.karina.purchase.restdto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePurchaseDTO {
    @NotNull(message = "Supplier tidak boleh kosong")
    private UUID purchaseSupplier;
    private List<ResourceTempDTO> purchaseResource; //only for purchaseType = 1
    private String purchaseNote;
}
