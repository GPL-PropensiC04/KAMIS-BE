package gpl.karina.asset.dto.request;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetAddDTO {
    private String platNomor;
    @NotNull(message = "Nama Aset tidak boleh kosong")
    private String assetName;
    @NotNull(message = "Deskripsi Aset tidak boleh kosong")
    private String assetDescription;
    @NotNull(message = "Tipe Aset tidak boleh kosong")
    private String assetType;
    @NotNull(message = "Harga Aset tidak boleh kosong")
    private Integer assetPrice;
    private MultipartFile foto;
    @Column(name = "foto_content_type")
    private String fotoContentType;
    private String status = "Available";
    private String tanggalPerolehan;
}
