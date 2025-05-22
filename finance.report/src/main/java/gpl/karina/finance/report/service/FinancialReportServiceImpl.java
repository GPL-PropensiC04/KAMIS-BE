package gpl.karina.finance.report.service;

import gpl.karina.finance.report.dto.response.FinancialSummaryResponseDTO;
import gpl.karina.finance.report.repository.LapkeuRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
public class FinancialReportServiceImpl implements FinancialReportService {

    private final LapkeuRepository lapkeuRepository;

    public FinancialReportServiceImpl(LapkeuRepository lapkeuRepository) {
        this.lapkeuRepository = lapkeuRepository;
    }

    @Override
    public FinancialSummaryResponseDTO getFinancialSummary(String range) throws Exception {
        // Tentukan rentang tanggal berdasarkan range
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (range.toUpperCase()) {
            case "THIS_MONTH":
                startDate = getStartOfMonth();
                break;
            case "THIS_QUARTER":
                startDate = getStartOfQuarter();
                break;
            case "THIS_YEAR":
                startDate = getStartOfYear();
                break;
            default:
                throw new IllegalArgumentException("Range tidak valid. Gunakan THIS_YEAR, THIS_QUARTER, atau THIS_MONTH.");
        }

        // Konversi LocalDate ke java.util.Date untuk operasi yang membutuhkan java.util.Date
        java.sql.Date startSqlDate = java.sql.Date.valueOf(startDate);
        java.sql.Date endSqlDate = java.sql.Date.valueOf(endDate);

        // Fetch total income and expenses based on range
        long totalIncomeFromDistribusi = getTotalIncomeFromDistribusi(startSqlDate, endSqlDate);
        long totalIncomeFromPenjualan = getTotalIncomeFromPenjualan(startSqlDate, endSqlDate);
        long totalIncome = totalIncomeFromDistribusi + totalIncomeFromPenjualan;  // Total income = from Distribusi + Penjualan

        long totalPurchase = getTotalExpenseByActivityType(2, startSqlDate, endSqlDate); // ActivityType 2 for PURCHASE
        long totalMaintenanceExpense = getTotalExpenseByActivityType(3, startSqlDate, endSqlDate); // ActivityType 3 for MAINTENANCE
        long totalProjectExpense = getTotalExpenseByActivityType(1, startSqlDate, endSqlDate); // ActivityType 1 for DISTRIBUSI

        long totalProfit = totalIncome - totalPurchase - totalMaintenanceExpense - totalProjectExpense;

        // Create the DTO with all the information
        FinancialSummaryResponseDTO summary = new FinancialSummaryResponseDTO();
        summary.setTotalIncome(totalIncome);
        summary.setTotalIncomeFromDistribusi(totalIncomeFromDistribusi);
        summary.setTotalIncomeFromPenjualan(totalIncomeFromPenjualan);
        summary.setTotalPurchase(totalPurchase);
        summary.setTotalMaintenanceExpense(totalMaintenanceExpense);
        summary.setTotalProjectExpense(totalProjectExpense);
        summary.setTotalProfit(totalProfit);

        return summary;
    }

    // Helper methods to calculate the start of the month, quarter, and year
    private LocalDate getStartOfMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }

    private LocalDate getStartOfQuarter() {
        int quarter = (LocalDate.now().getMonthValue() - 1) / 3 + 1;
        java.time.Month firstMonth = java.time.Month.of((quarter - 1) * 3 + 1);
        return LocalDate.of(LocalDate.now().getYear(), firstMonth, 1);
    }

    private LocalDate getStartOfYear() {
        return LocalDate.now().withDayOfYear(1);
    }

    // Helper methods to fetch total income and expenses for a range
    private long getTotalIncomeFromDistribusi(java.sql.Date startDate, java.sql.Date endDate) {
        Long incomeFromDistribusi = lapkeuRepository.getTotalIncomeFromDistribusi(startDate, endDate);
        return incomeFromDistribusi != null ? incomeFromDistribusi : 0L;
    }

    private long getTotalIncomeFromPenjualan(java.sql.Date startDate, java.sql.Date endDate) {
        Long incomeFromPenjualan = lapkeuRepository.getTotalIncomeFromPenjualan(startDate, endDate);
        return incomeFromPenjualan != null ? incomeFromPenjualan : 0L;
    }

    private long getTotalExpenseByActivityType(int activityType, java.sql.Date startDate, java.sql.Date endDate) {
        Long expense = lapkeuRepository.getTotalExpenseByActivityType(activityType, startDate, endDate);
        return expense != null ? expense : 0L;
    }

}
