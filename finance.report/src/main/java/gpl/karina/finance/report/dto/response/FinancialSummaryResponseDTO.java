package gpl.karina.finance.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialSummaryResponseDTO {
    private long totalIncome;                    // Total Income
    private long totalIncomeFromDistribusi;      // Income from Distribusi
    private long totalIncomeFromPenjualan;       // Income from Penjualan
    private long totalPurchase;                  // Total Purchase
    private long totalMaintenanceExpense;        // Total Maintenance Expense
    private long totalProjectExpense;            // Total Project Expense
    private long totalProfit;                    // Total Profit

    // Setters are automatically generated due to Lombok's @Data annotation
}
