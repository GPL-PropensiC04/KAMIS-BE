package gpl.karina.asset.dto.response;
import lombok.*;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
public class AssetResponseDTO {
    private String platNomor;
    private String nama;
    private String jenisAset;
    private String status;
    private Date tanggalPerolehan;
    private Float nilaiPerolehan;
    private String deskripsi;
    private String assetMaintenance;
// private List<String> historiMaintenance;
    private String gambarAsetBase64; // Menyimpan gambar dalam format base64
}
