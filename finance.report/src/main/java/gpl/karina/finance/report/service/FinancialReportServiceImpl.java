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
        LocalDate startCurrent, endCurrent, startPrevious, endPrevious;

        switch (range.toUpperCase()) {
            case "THIS_YEAR":
                startCurrent = now.withDayOfYear(1);
                endCurrent = now.withDayOfYear(now.lengthOfYear());
                startPrevious = startCurrent.minusYears(1);
                endPrevious = endCurrent.minusYears(1);
                break;

            case "THIS_QUARTER":
                int currentMonthValue = now.getMonthValue();
                int quarter = (currentMonthValue - 1) / 3 + 1;
                Month firstMonthOfQuarter = Month.of((quarter - 1) * 3 + 1);
                
                startCurrent = LocalDate.of(now.getYear(), firstMonthOfQuarter, 1);
                endCurrent = startCurrent.plusMonths(3).minusDays(1);
                
                startPrevious = startCurrent.minusYears(1);
                endPrevious = endCurrent.minusYears(1);
                break;

            case "THIS_MONTH":
                startCurrent = now.withDayOfMonth(1);
                endCurrent = now.withDayOfMonth(now.lengthOfMonth());
                
                // Untuk bulan sebelumnya secara akurat
                LocalDate prevMonthDate = now.minusMonths(1);
                startPrevious = prevMonthDate.withDayOfMonth(1);
                endPrevious = prevMonthDate.withDayOfMonth(prevMonthDate.lengthOfMonth());
                break;

            case "LAST_YEAR": // Implementasi baru
                startCurrent = LocalDate.of(now.getYear() - 1, 1, 1);     // 1 Januari tahun lalu
                endCurrent = LocalDate.of(now.getYear() - 1, 12, 31);   // 31 Desember tahun lalu
                
                startPrevious = startCurrent.minusYears(1);                // 1 Januari dua tahun lalu
                endPrevious = endCurrent.minusYears(1);                  // 31 Desember dua tahun lalu
                break;

            default:
                throw new IllegalArgumentException("Range tidak valid. Gunakan THIS_YEAR, THIS_QUARTER, THIS_MONTH, atau LAST_YEAR. Diterima: " + range);
        }

        // Konversi LocalDate ke java.util.Date untuk query
        Date startDateCurrent = Date.from(startCurrent.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateCurrent = Date.from(endCurrent.atTime(23, 59, 59, 999999999).atZone(ZoneId.systemDefault()).toInstant());
        Date startDatePrevious = Date.from(startPrevious.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDatePrevious = Date.from(endPrevious.atTime(23, 59, 59, 999999999).atZone(ZoneId.systemDefault()).toInstant());

        // --- Data Periode Saat Ini ---
        long totalIncomeFromDistribusiCurrent = getTotalIncomeFromDistribusi(startDateCurrent, endDateCurrent);
        long totalIncomeFromPenjualanCurrent = getTotalIncomeFromPenjualan(startDateCurrent, endDateCurrent);
        long totalIncomeCurrent = totalIncomeFromDistribusiCurrent + totalIncomeFromPenjualanCurrent;

        long totalPurchaseCurrent = getTotalExpenseByActivityType(2, startDateCurrent, endDateCurrent); // ActivityType 2 for PURCHASE
        long totalMaintenanceExpenseCurrent = getTotalExpenseByActivityType(3, startDateCurrent, endDateCurrent); // ActivityType 3 for MAINTENANCE
        long totalProjectExpenseCurrent = getTotalExpenseByActivityType(1, startDateCurrent, endDateCurrent); // ActivityType 1 for DISTRIBUSI/PROJECT

        long totalProfitCurrent = totalIncomeCurrent - totalPurchaseCurrent - totalMaintenanceExpenseCurrent - totalProjectExpenseCurrent;

        int countDistribusiCurrent = countIncomeFromDistribusi(startDateCurrent, endDateCurrent);
        int countPenjualanCurrent = countIncomeFromPenjualan(startDateCurrent, endDateCurrent);
        int countPurchaseCurrent = countExpenseByActivityType(2, startDateCurrent, endDateCurrent); 
        int countMaintenanceCurrent = countExpenseByActivityType(3, startDateCurrent, endDateCurrent); 
        
        int totalTransactionsCurrent = countDistribusiCurrent + countPenjualanCurrent + countPurchaseCurrent + countMaintenanceCurrent;

        // --- Data Periode Sebelumnya ---
        long totalIncomeFromDistribusiPrevious = getTotalIncomeFromDistribusi(startDatePrevious, endDatePrevious);
        long totalIncomeFromPenjualanPrevious = getTotalIncomeFromPenjualan(startDatePrevious, endDatePrevious);
        long totalIncomePrevious = totalIncomeFromDistribusiPrevious + totalIncomeFromPenjualanPrevious;

        long totalPurchasePrevious = getTotalExpenseByActivityType(2, startDatePrevious, endDatePrevious);
        long totalMaintenanceExpensePrevious = getTotalExpenseByActivityType(3, startDatePrevious, endDatePrevious);
        long totalProjectExpensePrevious = getTotalExpenseByActivityType(1, startDatePrevious, endDatePrevious);
        
        long totalProfitPrevious = totalIncomePrevious - totalPurchasePrevious - totalMaintenanceExpensePrevious - totalProjectExpensePrevious;

        int countDistribusiPrevious = countIncomeFromDistribusi(startDatePrevious, endDatePrevious);
        int countPenjualanPrevious = countIncomeFromPenjualan(startDatePrevious, endDatePrevious);
        int countPurchasePrevious = countExpenseByActivityType(2, startDatePrevious, endDatePrevious);
        int countMaintenancePrevious = countExpenseByActivityType(3, startDatePrevious, endDatePrevious);

        int totalTransactionsPrevious = countDistribusiPrevious + countPenjualanPrevious + countPurchasePrevious + countMaintenancePrevious;

        // --- Hitung Perubahan Persentase ---
        double transactionPercentageChange = calculatePercentageChange(totalTransactionsCurrent, totalTransactionsPrevious);
        double profitPercentageChange = calculatePercentageChange(totalProfitCurrent, totalProfitPrevious);

        // --- Set DTO ---
        FinancialSummaryResponseDTO summary = new FinancialSummaryResponseDTO();
        summary.setTotalIncome(totalIncomeCurrent);
        summary.setTotalIncomeFromDistribusi(totalIncomeFromDistribusiCurrent);
        summary.setTotalIncomeFromPenjualan(totalIncomeFromPenjualanCurrent);
        summary.setTotalPurchase(totalPurchaseCurrent);
        summary.setTotalMaintenanceExpense(totalMaintenanceExpenseCurrent);
        summary.setTotalProjectExpense(totalProjectExpenseCurrent);
        summary.setTotalProfit(totalProfitCurrent);

        summary.setTotalTransactions(totalTransactionsCurrent);
        summary.setTransactionPercentageChange(transactionPercentageChange);
        summary.setProfitPercentageChange(profitPercentageChange);

        return summary;
    }

    // Helper method untuk menghitung perubahan persentase (tetap sama)
    private double calculatePercentageChange(long currentValue, long previousValue) {
        if (previousValue == 0) {
            return currentValue > 0 ? 100.0 : (currentValue == 0 ? 0.0 : -100.0); 
        }
        return ((double) (currentValue - previousValue) / previousValue) * 100.0;
    }

    // Overload untuk tipe int jika currentValue dan previousValue adalah int (tetap sama)
    private double calculatePercentageChange(int currentValue, int previousValue) {
        if (previousValue == 0) {
            return currentValue > 0 ? 100.0 : (currentValue == 0 ? 0.0 : -100.0); 
        }
        return ((double) (currentValue - previousValue) / previousValue) * 100.0;
    }

    // Helper methods untuk mengambil data dari repository (tetap sama)
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

    private int countIncomeFromDistribusi(Date startDate, Date endDate) {
        Integer count = lapkeuRepository.countIncomeFromDistribusi(startDate, endDate);
        return count != null ? count : 0;
    }

    private int countIncomeFromPenjualan(Date startDate, Date endDate) {
        Integer count = lapkeuRepository.countIncomeFromPenjualan(startDate, endDate);
        return count != null ? count : 0;
    }

    private int countExpenseByActivityType(int activityType, Date startDate, Date endDate) {
        Integer count = lapkeuRepository.countExpenseByActivityType(activityType, startDate, endDate);
        return count != null ? count : 0;
    }

}
