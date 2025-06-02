package gpl.karina.asset.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetMaintenanceRequestDTO {
    private String platNomor;
    private Date tanggalMulaiMaintenance;
    private Date tanggalSelesaiMaintenance;
    private String deskripsiPekerjaan;
    private Float biaya;
    private String notes;
}
