package gpl.karina.resource.restdto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddResourceDTO {
    @NotNull(message = "Nama barang tidak boleh kosong")
    private String resourceName;
    @NotNull(message = "Deskripsi barang tidak boleh kosong")
    private String resourceDescription;
    @NotNull(message = "Supplier barang tidak boleh kosong")
    private String resourceSupplier;
    @NotNull(message = "Stok barang tidak boleh kosong")
    private Integer resourceStock;
    @NotNull(message = "Harga barang tidak boleh kosong")
    @Min(value = 0, message = "Harga barang tidak boleh kurang dari 0")
    private Integer resourcePrice;
}
