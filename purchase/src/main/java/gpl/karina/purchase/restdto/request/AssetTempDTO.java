package gpl.karina.purchase.restdto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetTempDTO {
    private Long assetId;
    @NotNull(message = "Nama Aset tidak boleh kosong")
    private String assetName;
    @NotNull(message = "Deskripsi Aset tidak boleh kosong")
    private String assetDescription;
    @NotNull(message = "Tipe Aset tidak boleh kosong")
    private String assetType;
    @NotNull(message = "Harga Aset tidak boleh kosong")
    private Integer assetPrice;
}
