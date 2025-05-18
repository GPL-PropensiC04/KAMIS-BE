package gpl.karina.finance.report.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartPengeluaranResponseDTO {
	private String activityType;
    private Long totalPengeluaran;

    public ChartPengeluaranResponseDTO(Integer activityTypeCode, Long total) {
        this.activityType = switch (activityTypeCode) {
            case 1 -> "Distribusi";
            case 2 -> "Pembelian";
            case 3 -> "Maintenance";
            default -> "UNKNOWN";
        };
        this.totalPengeluaran = total;
    }
}
