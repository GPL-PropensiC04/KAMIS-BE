package gpl.karina.purchase.restdto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddPurchaseDTO {
    @NotNull(message = "Supplier tidak boleh kosong")
    private UUID purchaseSupplier;
    @NotNull(message = "Tipe barang tidak boleh kosong")
    private boolean purchaseType; // Value 0 = Aset, Value 1 = Resource

    private Long purchaseAsset; //only for purchaseType = 0
    private List<ResourceTempDTO> purchaseResource; //only for purchaseType = 1

    private String purchaseNote;
}
