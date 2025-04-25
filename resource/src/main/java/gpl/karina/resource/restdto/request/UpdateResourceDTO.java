package gpl.karina.resource.restdto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateResourceDTO {
    @NotNull(message = "Deskripsi barang tidak boleh kosong")
    private String resourceDescription;
    @NotNull(message = "Harga barang tidak boleh kosong")
    @Min(value = 0, message = "Harga barang tidak boleh kurang dari 0")
    private Integer resourcePrice;
    @NotNull(message = "Stok barang tidak boleh kosong")
    @Min(value = 0, message = "Stok barang tidak boleh kurang dari 0")
    private Integer resourceStock;
}
