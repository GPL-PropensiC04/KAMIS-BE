package gpl.karina.asset.dto.response;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
public class AssetResponseDTO {
    private String platNomor;
    private String nama;
    private String jenisAset;
    private String status;
    private Date tanggalPerolehan;
    private Integer nilaiPerolehan;
    private String deskripsi;
    private String assetMaintenance;
    private String fotoContentType;
    private String fotoUrl;
    private UUID supplierId;
}
