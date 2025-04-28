package gpl.karina.asset.dto.response;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
public class AssetListResponseDTO {
    private String platNomor;
    private String nama;
    private String status;
    private Integer nilaiPerolehan;
    private Date tanggalPerolehan;
    private UUID supplierId;
    private Date lastMaintenance;
}
