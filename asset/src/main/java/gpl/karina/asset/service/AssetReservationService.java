package gpl.karina.asset.service;

import gpl.karina.asset.model.AssetReservation;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssetReservationService {

    /**
     * Create a new asset reservation
     */
    AssetReservation createReservation(String platNomor, String projectId, Date startDate, Date endDate);

    /**
     * Create multiple asset reservations for a project
     */
    List<AssetReservation> createReservations(List<String> platNomors, String projectId, Date startDate, Date endDate);

    /**
     * Update the status of an existing reservation
     */
    AssetReservation updateReservationStatus(UUID reservationId, String status);

    /**
     * Update the status of all reservations for a project
     */
    List<AssetReservation> updateProjectReservationStatus(String projectId, String status);

    /**
     * Get all reservations for a specific asset
     */
    List<AssetReservation> getReservationsByAsset(String platNomor);

    /**
     * Get all reservations for a specific project
     */
    List<AssetReservation> getReservationsByProject(String projectId);

    /**
     * Check if an asset is available for a specific time period
     */
    boolean isAssetAvailable(String platNomor, Date startDate, Date endDate);

    /**
     * Check if an asset is available for a specific time period, excluding a
     * specific project's reservations
     */
    boolean isAssetAvailableExcludingProject(String platNomor, Date startDate, Date endDate, String excludeProjectId);

    /**
     * Check availability for multiple assets in a single call
     */
    Map<String, Boolean> checkAssetsAvailability(List<String> platNomors, Date startDate, Date endDate);

    /**
     * Check availability for multiple assets excluding a project's reservations
     */
    Map<String, Boolean> checkAssetsAvailabilityExcludingProject(List<String> platNomors, Date startDate, Date endDate,
            String excludeProjectId);
}