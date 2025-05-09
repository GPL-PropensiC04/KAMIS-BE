package gpl.karina.asset.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceResponseDTO {
    private Long id;
    private Date tanggalMulaiMaintenance;
    private Date tanggalSelesaiMaintenance;
    private String deskripsiPekerjaan;
    private Float biaya;
    private String status;
    private String platNomor;
    private String namaAset;
}