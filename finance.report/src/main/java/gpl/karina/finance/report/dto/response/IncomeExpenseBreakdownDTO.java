package gpl.karina.finance.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeExpenseBreakdownDTO {
    private String period;
    // Key: activity type name (e.g., "Distribusi", "Penjualan"), Value: amount
    private Map<String, Long> pemasukanBreakdown;
    // Key: activity type name (e.g., "Distribusi", "Pembelian", "Maintenance"), Value: amount
    private Map<String, Long> pengeluaranBreakdown;
    private Long totalPemasukan;
    private Long totalPengeluaran;
}