package gpl.karina.finance.report.service;

import gpl.karina.finance.report.dto.response.FinancialSummaryResponseDTO;
import gpl.karina.finance.report.repository.LapkeuRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

@Service
public class FinancialReportServiceImpl implements FinancialReportService {

    private final LapkeuRepository lapkeuRepository;

    public FinancialReportServiceImpl(LapkeuRepository lapkeuRepository) {
        this.lapkeuRepository = lapkeuRepository;
    }

    @Override
    public FinancialSummaryResponseDTO getFinancialSummary(String range) throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        switch (range.toUpperCase()) {
            case "THIS_YEAR":
                startDate = now.withDayOfYear(1);
                endDate = now.withDayOfYear(now.lengthOfYear());
                break;

            case "THIS_QUARTER":
                int currentMonthValue = now.getMonthValue();
                int quarter = (currentMonthValue - 1) / 3 + 1;
                Month firstMonthOfQuarter = Month.of((quarter - 1) * 3 + 1);
                
                startDate = LocalDate.of(now.getYear(), firstMonthOfQuarter, 1);
                endDate = startDate.plusMonths(3).minusDays(1); // Konsisten dengan metode pertama
                break;

            case "THIS_MONTH":
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
                break;

            default:
                throw new IllegalArgumentException("Range tidak valid. Gunakan THIS_YEAR, THIS_QUARTER, atau THIS_MONTH. Diterima: " + range);
        }

        // Konversi LocalDate ke java.util.Date
        Date startDateForQuery = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateForQuery = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        // Fetch total income and expenses based on range
        // PENTING: Metode helper di bawah ini sekarang harus menerima java.util.Date
        long totalIncomeFromDistribusi = getTotalIncomeFromDistribusi(startDateForQuery, endDateForQuery);
        long totalIncomeFromPenjualan = getTotalIncomeFromPenjualan(startDateForQuery, endDateForQuery);
        long totalIncome = totalIncomeFromDistribusi + totalIncomeFromPenjualan;

        long totalPurchase = getTotalExpenseByActivityType(2, startDateForQuery, endDateForQuery); // ActivityType 2 for PURCHASE
        long totalMaintenanceExpense = getTotalExpenseByActivityType(3, startDateForQuery, endDateForQuery); // ActivityType 3 for MAINTENANCE
        long totalProjectExpense = getTotalExpenseByActivityType(1, startDateForQuery, endDateForQuery); // ActivityType 1 for DISTRIBUSI/PROJECT

        long totalProfit = totalIncome - totalPurchase - totalMaintenanceExpense - totalProjectExpense;

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
