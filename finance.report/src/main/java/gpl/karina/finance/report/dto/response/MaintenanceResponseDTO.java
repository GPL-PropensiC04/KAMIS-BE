package gpl.karina.finance.report.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceResponseDTO {
    private Long id;
    private Float biaya;
    private String platNomor;
    private String namaAset;
    private Date tanggalMulaiMaintenance;
}