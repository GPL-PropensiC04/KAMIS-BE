package gpl.karina.purchase.restdto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceTempDTO {
    private Long resourceId;
    @NotNull(message = "Nama barang tidak boleh kosong")
    private String resourceName;
    @NotNull(message = "Jumlah barang tidak boleh kosong")
    private Integer resourceTotal;
    @NotNull(message = "Harga barang tidak boleh kosong")
    private Integer resourcePrice;
}
