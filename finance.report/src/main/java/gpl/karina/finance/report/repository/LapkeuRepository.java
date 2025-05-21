package gpl.karina.finance.report.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gpl.karina.finance.report.model.Lapkeu;

@Repository
public interface LapkeuRepository extends JpaRepository<Lapkeu, String> {

    @Query("SELECT l.activityType, SUM(l.pengeluaran) " +
       "FROM Lapkeu l " +
       "WHERE l.pengeluaran IS NOT NULL " +
       "GROUP BY l.activityType")
    List<Object[]> getTotalPengeluaranPerActivityType();

    @Query("SELECT l.activityType, SUM(l.pengeluaran) " +
        "FROM Lapkeu l " +
        "WHERE l.pengeluaran IS NOT NULL " +
        "AND l.paymentDate >= :startDate AND l.paymentDate <= :endDate " +
        "GROUP BY l.activityType")
    List<Object[]> getTotalPengeluaranPerActivityTypeBetweenDates(
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    // =========================
    // ======== WEEKLY ========
    // =========================

    @Query("SELECT l.paymentDate, SUM(l.pemasukan), SUM(l.pengeluaran) " +
       "FROM Lapkeu l " +
       "WHERE l.paymentDate BETWEEN :startDate AND :endDate " +
       "GROUP BY l.paymentDate ORDER BY l.paymentDate")
    List<Object[]> getIncomeExpenseRawByDay(@Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate);


    // =========================
    // ======== MONTHLY ========
    // =========================

    @Query("SELECT TO_CHAR(l.paymentDate, 'YYYY-MM') AS period, SUM(l.pemasukan), SUM(l.pengeluaran) " +
           "FROM Lapkeu l " +
           "WHERE l.paymentDate >= :startDate AND l.paymentDate <= :endDate " +
           "GROUP BY TO_CHAR(l.paymentDate, 'YYYY-MM') " +
           "ORDER BY period")
    List<Object[]> getIncomeExpenseMonthlyFiltered(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // ==========================
    // ======= QUARTERLY ========
    // ==========================

    @Query("SELECT CONCAT(EXTRACT(YEAR FROM l.paymentDate), '-Q', EXTRACT(QUARTER FROM l.paymentDate)) AS period, " +
           "SUM(l.pemasukan), SUM(l.pengeluaran) " +
           "FROM Lapkeu l " +
           "WHERE l.paymentDate >= :startDate AND l.paymentDate <= :endDate " +
           "GROUP BY EXTRACT(YEAR FROM l.paymentDate), EXTRACT(QUARTER FROM l.paymentDate) " +
           "ORDER BY period")
    List<Object[]> getIncomeExpenseQuarterlyFiltered(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // ==========================
    // ========= YEARLY =========
    // ==========================

    @Query("SELECT CAST(EXTRACT(YEAR FROM l.paymentDate) AS string) AS period, SUM(l.pemasukan), SUM(l.pengeluaran) " +
           "FROM Lapkeu l " +
           "WHERE l.paymentDate >= :startDate AND l.paymentDate <= :endDate " +
           "GROUP BY EXTRACT(YEAR FROM l.paymentDate) " +
           "ORDER BY period")
    List<Object[]> getIncomeExpenseYearlyFiltered(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


    // Query to get total income for a specific date range
    @Query("SELECT SUM(l.pemasukan) " +
           "FROM Lapkeu l " +
           "WHERE l.pemasukan IS NOT NULL " +
           "AND l.paymentDate BETWEEN :startDate AND :endDate")
    Long getTotalIncomeBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // Query to get total expenses by activity type for a specific date range
    @Query("SELECT SUM(l.pengeluaran) " +
           "FROM Lapkeu l " +
           "WHERE l.pengeluaran IS NOT NULL " +
           "AND l.activityType = :activityType " +
           "AND l.paymentDate BETWEEN :startDate AND :endDate")
    Long getTotalExpenseByActivityType(@Param("activityType") int activityType,
                                       @Param("startDate") Date startDate, 
                                       @Param("endDate") Date endDate);

    // New query for total income from Sales (ActivityType 0 for Penjualan)
    @Query("SELECT SUM(l.pemasukan) " +
           "FROM Lapkeu l " +
           "WHERE l.pemasukan IS NOT NULL " +
           "AND l.activityType = 0 " + // ActivityType 0 for PENJUALAN
           "AND l.paymentDate BETWEEN :startDate AND :endDate")
    Long getTotalSalesIncomeBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // New query for total income from Distribution (ActivityType 1 for DISTRIBUSI)
    @Query("SELECT SUM(l.pemasukan) " +
           "FROM Lapkeu l " +
           "WHERE l.pemasukan IS NOT NULL " +
           "AND l.activityType = 1 " + // ActivityType 1 for DISTRIBUSI
           "AND l.paymentDate BETWEEN :startDate AND :endDate")
    Long getTotalDistributionIncomeBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // Query to get total income for DISTRIBUSI (activityType = 1)
    @Query("SELECT SUM(l.pemasukan) " +
           "FROM Lapkeu l " +
           "WHERE l.pemasukan IS NOT NULL " +
           "AND l.activityType = 1 " +  // ActivityType 1 for DISTRIBUSI
           "AND l.paymentDate BETWEEN :startDate AND :endDate")
    Long getTotalIncomeFromDistribusi(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // Query to get total income for PENJUALAN (activityType = 0)
    @Query("SELECT SUM(l.pemasukan) " +
           "FROM Lapkeu l " +
           "WHERE l.pemasukan IS NOT NULL " +
           "AND l.activityType = 0 " +  // ActivityType 0 for PENJUALAN
           "AND l.paymentDate BETWEEN :startDate AND :endDate")
    Long getTotalIncomeFromPenjualan(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT SUM(l.pemasukan) " +
           "FROM Lapkeu l " +
           "WHERE l.pemasukan IS NOT NULL " +
           "AND l.activityType = :activityType " +
           "AND l.paymentDate BETWEEN :startDate AND :endDate")
    Long getTotalIncomeByActivityType(@Param("activityType") int activityType,
                                     @Param("startDate") Date startDate, 
                                     @Param("endDate") Date endDate);


}
