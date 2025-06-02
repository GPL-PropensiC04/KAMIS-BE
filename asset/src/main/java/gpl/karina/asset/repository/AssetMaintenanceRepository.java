package gpl.karina.asset.repository;

import gpl.karina.asset.model.AssetMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface AssetMaintenanceRepository extends JpaRepository<AssetMaintenance, UUID> {
    List<AssetMaintenance> findByPlatNomor(String platNomor);

    List<AssetMaintenance> findByStatus(String status);

    List<AssetMaintenance> findByPlatNomorAndStatus(String platNomor, String status);

    @Query("SELECT am FROM AssetMaintenance am WHERE am.platNomor = :platNomor " +
            "AND (am.status = 'Dijadwalkan' OR am.status = 'Sedang Berlangsung') " +
            "AND ((am.tanggalMulaiMaintenance <= :tanggalSelesai AND am.tanggalSelesaiMaintenance >= :tanggalMulai) " +
            "OR (am.tanggalMulaiMaintenance >= :tanggalMulai AND am.tanggalMulaiMaintenance <= :tanggalSelesai) " +
            "OR (am.tanggalSelesaiMaintenance >= :tanggalMulai AND am.tanggalSelesaiMaintenance <= :tanggalSelesai))")
    List<AssetMaintenance> findOverlappingMaintenance(
            @Param("platNomor") String platNomor,
            @Param("tanggalMulai") Date tanggalMulai,
            @Param("tanggalSelesai") Date tanggalSelesai);

    @Query("SELECT am FROM AssetMaintenance am WHERE am.platNomor = :platNomor " +
            "AND (am.status = 'Dijadwalkan' OR am.status = 'Sedang Berlangsung')")
    List<AssetMaintenance> findActiveMaintenanceForAsset(@Param("platNomor") String platNomor);

    @Query("SELECT am FROM AssetMaintenance am WHERE am.platNomor IN :platNomors " +
            "AND (am.status = 'Dijadwalkan' OR am.status = 'Sedang Berlangsung') " +
            "AND ((am.tanggalMulaiMaintenance <= :tanggalSelesai AND am.tanggalSelesaiMaintenance >= :tanggalMulai) " +
            "OR (am.tanggalMulaiMaintenance >= :tanggalMulai AND am.tanggalMulaiMaintenance <= :tanggalSelesai) " +
            "OR (am.tanggalSelesaiMaintenance >= :tanggalMulai AND am.tanggalSelesaiMaintenance <= :tanggalSelesai))")
    List<AssetMaintenance> findOverlappingMaintenanceForAssets(
            @Param("platNomors") List<String> platNomors,
            @Param("tanggalMulai") Date tanggalMulai,
            @Param("tanggalSelesai") Date tanggalSelesai);

    @Query("SELECT am FROM AssetMaintenance am WHERE am.tanggalMulaiMaintenance >= :tanggalMulai AND am.tanggalMulaiMaintenance <= :tanggalSelesai")
    List<AssetMaintenance> findMaintenanceByDateRange(
            @Param("tanggalMulai") Date tanggalMulai,
            @Param("tanggalSelesai") Date tanggalSelesai);
}
