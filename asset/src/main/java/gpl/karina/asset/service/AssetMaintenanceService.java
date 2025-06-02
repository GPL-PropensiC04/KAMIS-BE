package gpl.karina.asset.service;

import gpl.karina.asset.model.AssetMaintenance;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssetMaintenanceService {
    
    /**
     * Creates a new maintenance schedule for an asset
     */
    AssetMaintenance createMaintenance(String platNomor, Date tanggalMulaiMaintenance, Date tanggalSelesaiMaintenance, 
                                      String deskripsiPekerjaan, Float biaya, String notes);
    
    /**
     * Updates the status of a maintenance
     */
    AssetMaintenance updateMaintenanceStatus(UUID maintenanceId, String status);
    
    /**
     * Updates maintenance details
     */
    AssetMaintenance updateMaintenance(UUID maintenanceId, Date tanggalMulaiMaintenance, Date tanggalSelesaiMaintenance,
                                      String deskripsiPekerjaan, Float biaya, String notes);
    
    /**
     * Gets all maintenance records for a specific asset
     */
    List<AssetMaintenance> getMaintenanceByAsset(String platNomor);
    
    /**
     * Gets maintenance records by status
     */
    List<AssetMaintenance> getMaintenanceByStatus(String status);
    
    /**
     * Checks if an asset is available for maintenance during a specific period
     * (not reserved for projects and not under other maintenance)
     */
    boolean isAssetAvailableForMaintenance(String platNomor, Date tanggalMulaiMaintenance, Date tanggalSelesaiMaintenance);
    
    /**
     * Checks availability of multiple assets for maintenance
     */
    Map<String, Boolean> checkAssetsAvailabilityForMaintenance(List<String> platNomors, Date tanggalMulaiMaintenance, Date tanggalSelesaiMaintenance);
    
    /**
     * Gets detailed availability information including conflicts
     */
    Map<String, String> getAssetAvailabilityDetails(List<String> platNomors, Date tanggalMulaiMaintenance, Date tanggalSelesaiMaintenance);
    
    /**
     * Gets all maintenance records within a date range
     */
    List<AssetMaintenance> getMaintenanceByDateRange(Date tanggalMulai, Date tanggalSelesai);
    
    /**
     * Cancels a maintenance
     */
    AssetMaintenance cancelMaintenance(UUID maintenanceId);
}
