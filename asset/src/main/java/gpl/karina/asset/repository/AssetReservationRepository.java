package gpl.karina.asset.repository;

import gpl.karina.asset.model.AssetReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface AssetReservationRepository extends JpaRepository<AssetReservation, UUID> {
        List<AssetReservation> findByPlatNomor(String platNomor);

        List<AssetReservation> findByProjectId(String projectId);

        List<AssetReservation> findByProjectIdAndReservationStatus(String projectId, String reservationStatus);

        // Optional: Add this method for more specific filtering
        @Query("SELECT ar FROM AssetReservation ar WHERE ar.platNomor = :platNomor AND ar.reservationStatus IN ('Direncanakan', 'Dilaksanakan')")
        List<AssetReservation> findActiveReservationsByPlatNomor(@Param("platNomor") String platNomor);

        @Query("SELECT ar FROM AssetReservation ar WHERE ar.platNomor = :platNomor " +
                        "AND (ar.reservationStatus = 'Direncanakan' OR ar.reservationStatus = 'Dilaksanakan') " +
                        "AND ((ar.startDate <= :endDate AND ar.endDate >= :startDate) " +
                        "OR (ar.startDate >= :startDate AND ar.startDate <= :endDate) " +
                        "OR (ar.endDate >= :startDate AND ar.endDate <= :endDate))")
        List<AssetReservation> findOverlappingReservations(
                        @Param("platNomor") String platNomor,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        @Query("SELECT ar FROM AssetReservation ar WHERE ar.platNomor = :platNomor " +
                        "AND (ar.reservationStatus = 'Direncanakan' OR ar.reservationStatus = 'Dilaksanakan') " +
                        "AND ((ar.startDate <= :endDate AND ar.endDate >= :startDate) " +
                        "OR (ar.startDate >= :startDate AND ar.startDate <= :endDate) " +
                        "OR (ar.endDate >= :startDate AND ar.endDate <= :endDate)) " +
                        "AND ar.projectId != :excludeProjectId")
        List<AssetReservation> findOverlappingReservationsExcludingProject(
                        @Param("platNomor") String platNomor,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("excludeProjectId") String excludeProjectId);

        @Query(value = "SELECT r FROM AssetReservation r WHERE r.platNomor = :platNomor " +
                        "AND (r.reservationStatus = 'Direncanakan' OR r.reservationStatus = 'Dilaksanakan')")
        List<AssetReservation> findActiveReservationsForAsset(@Param("platNomor") String platNomor);
}