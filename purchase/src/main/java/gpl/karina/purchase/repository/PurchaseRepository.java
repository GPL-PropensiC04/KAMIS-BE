package gpl.karina.purchase.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gpl.karina.purchase.model.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, String> {
    long countByPurchaseSubmissionDate(Date purchaseSubmissionDate);
    // Purchase findByIdAndNotDeleted(String purchaseId);
    List<Purchase> findAllByPurchaseSupplier(UUID purchaseSupplier);


    // ===============================
    // ======= WEEKLY QUERIES =======
    // ===============================

    @Query("SELECT p.purchaseSubmissionDate, COUNT(p) " +
       "FROM Purchase p " +
       "WHERE p.purchaseSubmissionDate BETWEEN :startDate AND :endDate " +
       "AND p.purchaseStatus IN :statuses " +
       "GROUP BY p.purchaseSubmissionDate")
    List<Object[]> getDailyPurchaseCountInStatus(@Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate,
                                             @Param("statuses") List<String> statuses);

    @Query("SELECT p.purchaseSubmissionDate, COUNT(p) " +
       "FROM Purchase p " +
       "WHERE p.purchaseSubmissionDate BETWEEN :startDate AND :endDate " +
       "AND p.purchaseStatus NOT IN :statuses " +
       "GROUP BY p.purchaseSubmissionDate")
    List<Object[]> getDailyPurchaseCountExcludeStatus(@Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate,
                                                  @Param("statuses") List<String> statuses);


    // ===============================
    // ======= MONTHLY QUERIES =======
    // ===============================

    @Query("SELECT TO_CHAR(p.purchaseSubmissionDate, 'YYYY-MM'), COUNT(p) " +
           "FROM Purchase p " +
           "WHERE p.purchaseSubmissionDate BETWEEN :startDate AND :endDate " +
           "AND p.purchaseStatus IN :statuses " +
           "GROUP BY TO_CHAR(p.purchaseSubmissionDate, 'YYYY-MM') " +
           "ORDER BY 1")
    List<Object[]> getMonthlyPurchaseCountInStatus(@Param("startDate") Date startDate,
                                                   @Param("endDate") Date endDate,
                                                   @Param("statuses") List<String> statuses);

    @Query("SELECT TO_CHAR(p.purchaseSubmissionDate, 'YYYY-MM'), COUNT(p) " +
           "FROM Purchase p " +
           "WHERE p.purchaseSubmissionDate BETWEEN :startDate AND :endDate " +
           "AND p.purchaseStatus NOT IN :statuses " +
           "GROUP BY TO_CHAR(p.purchaseSubmissionDate, 'YYYY-MM') " +
           "ORDER BY 1")
    List<Object[]> getMonthlyPurchaseCountExcludeStatus(@Param("startDate") Date startDate,
                                                        @Param("endDate") Date endDate,
                                                        @Param("statuses") List<String> statuses);

    // ================================
    // ======= QUARTERLY QUERIES ======
    // ================================

    @Query("SELECT CONCAT(EXTRACT(YEAR FROM p.purchaseSubmissionDate), '-Q', EXTRACT(QUARTER FROM p.purchaseSubmissionDate)), COUNT(p) " +
           "FROM Purchase p " +
           "WHERE p.purchaseSubmissionDate BETWEEN :startDate AND :endDate " +
           "AND p.purchaseStatus IN :statuses " +
           "GROUP BY EXTRACT(YEAR FROM p.purchaseSubmissionDate), EXTRACT(QUARTER FROM p.purchaseSubmissionDate) " +
           "ORDER BY 1")
    List<Object[]> getQuarterlyPurchaseCountInStatus(@Param("startDate") Date startDate,
                                                     @Param("endDate") Date endDate,
                                                     @Param("statuses") List<String> statuses);

    @Query("SELECT CONCAT(EXTRACT(YEAR FROM p.purchaseSubmissionDate), '-Q', EXTRACT(QUARTER FROM p.purchaseSubmissionDate)), COUNT(p) " +
           "FROM Purchase p " +
           "WHERE p.purchaseSubmissionDate BETWEEN :startDate AND :endDate " +
           "AND p.purchaseStatus NOT IN :statuses " +
           "GROUP BY EXTRACT(YEAR FROM p.purchaseSubmissionDate), EXTRACT(QUARTER FROM p.purchaseSubmissionDate) " +
           "ORDER BY 1")
    List<Object[]> getQuarterlyPurchaseCountExcludeStatus(@Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate,
                                                          @Param("statuses") List<String> statuses);

    // =============================
    // ======== YEARLY QUERIES =====
    // =============================

    @Query("SELECT CAST(EXTRACT(YEAR FROM p.purchaseSubmissionDate) AS string), COUNT(p) " +
           "FROM Purchase p " +
           "WHERE p.purchaseSubmissionDate BETWEEN :startDate AND :endDate " +
           "AND p.purchaseStatus IN :statuses " +
           "GROUP BY EXTRACT(YEAR FROM p.purchaseSubmissionDate) " +
           "ORDER BY 1")
    List<Object[]> getYearlyPurchaseCountInStatus(@Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate,
                                                  @Param("statuses") List<String> statuses);

    @Query("SELECT CAST(EXTRACT(YEAR FROM p.purchaseSubmissionDate) AS string), COUNT(p) " +
           "FROM Purchase p " +
           "WHERE p.purchaseSubmissionDate BETWEEN :startDate AND :endDate " +
           "AND p.purchaseStatus NOT IN :statuses " +
           "GROUP BY EXTRACT(YEAR FROM p.purchaseSubmissionDate) " +
           "ORDER BY 1")
    List<Object[]> getYearlyPurchaseCountExcludeStatus(@Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate,
                                                       @Param("statuses") List<String> statuses);
}
