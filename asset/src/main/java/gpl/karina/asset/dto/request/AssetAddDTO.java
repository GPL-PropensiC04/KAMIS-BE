package gpl.karina.asset.dto.request;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetAddDTO {
    @NotBlank(message = "Plat Nomor tidak boleh kosong")
    private String platNomor;

    @NotBlank(message = "Nama aset tidak boleh kosong")
    private String assetName;

    @NotBlank(message = "Deskripsi aset tidak boleh kosong")
    private String assetDescription;

    @NotBlank(message = "Tipe aset tidak boleh kosong")
    private String assetType;

    @NotNull(message = "Harga aset tidak boleh kosong")
    private Integer assetPrice;

    @NotBlank(message = "Status aset tidak boleh kosong")
    private String status;
    
    private String tanggalPerolehan;
    
    private MultipartFile foto;
    
    // Use String for supplierID in DTO
    @NotBlank(message = "Supplier ID tidak boleh kosong")
    private String supplierId;
}
