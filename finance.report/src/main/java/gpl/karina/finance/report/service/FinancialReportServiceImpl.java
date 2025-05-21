package gpl.karina.finance.report.service;

import gpl.karina.finance.report.dto.response.FinancialSummaryResponseDTO;
import gpl.karina.finance.report.repository.LapkeuRepository;
import org.springframework.stereotype.Service;

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
        Date startDate;
        Date endDate = new Date();

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

        // Fetch total income and expenses based on range
        long totalIncomeFromDistribusi = getTotalIncomeFromDistribusi(startDate, endDate);
        long totalIncomeFromPenjualan = getTotalIncomeFromPenjualan(startDate, endDate);
        long totalIncome = totalIncomeFromDistribusi + totalIncomeFromPenjualan;  // Total income = from Distribusi + Penjualan

        long totalPurchase = getTotalExpenseByActivityType(2, startDate, endDate); // ActivityType 2 for PURCHASE
        long totalMaintenanceExpense = getTotalExpenseByActivityType(3, startDate, endDate); // ActivityType 3 for MAINTENANCE
        long totalProjectExpense = getTotalExpenseByActivityType(1, startDate, endDate); // ActivityType 1 for DISTRIBUSI

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
    private Date getStartOfMonth() {
        return java.sql.Date.valueOf(java.time.LocalDate.now().withDayOfMonth(1));
    }

    private Date getStartOfQuarter() {
        int quarter = (java.time.LocalDate.now().getMonthValue() - 1) / 3 + 1;
        java.time.Month firstMonth = java.time.Month.of((quarter - 1) * 3 + 1);
        return java.sql.Date.valueOf(java.time.LocalDate.of(java.time.LocalDate.now().getYear(), firstMonth, 1));
    }

    private Date getStartOfYear() {
        return java.sql.Date.valueOf(java.time.LocalDate.now().withDayOfYear(1));
    }

    // Helper methods to fetch total income and expenses for a range
    private long getTotalIncomeFromDistribusi(Date startDate, Date endDate) {
        Long incomeFromDistribusi = lapkeuRepository.getTotalIncomeFromDistribusi(startDate, endDate);
        return incomeFromDistribusi != null ? incomeFromDistribusi : 0L;
    }

    private long getTotalIncomeFromPenjualan(Date startDate, Date endDate) {
        Long incomeFromPenjualan = lapkeuRepository.getTotalIncomeFromPenjualan(startDate, endDate);
        return incomeFromPenjualan != null ? incomeFromPenjualan : 0L;
    }

    private long getTotalExpenseByActivityType(int activityType, Date startDate, Date endDate) {
        Long expense = lapkeuRepository.getTotalExpenseByActivityType(activityType, startDate, endDate);
        return expense != null ? expense : 0L;
    }
}
