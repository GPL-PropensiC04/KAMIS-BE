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

}
