package gpl.karina.finance.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LapkeuSummaryResponseDTO {
    private int totalTransaksi;
    private long totalPemasukan;
    private long totalPengeluaran;
    private long totalProfit;
}