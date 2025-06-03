package gpl.karina.asset.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetMaintenanceAvailabilityRequestDTO {
    private List<String> platNomors;
    private Date tanggalMulaiMaintenance;
    private Date tanggalSelesaiMaintenance;
}
