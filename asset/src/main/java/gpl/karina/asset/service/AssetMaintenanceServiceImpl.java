package gpl.karina.asset.service;

import gpl.karina.asset.model.Asset;
import gpl.karina.asset.model.AssetMaintenance;
import gpl.karina.asset.model.AssetReservation;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.repository.AssetMaintenanceRepository;
import gpl.karina.asset.repository.AssetReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetMaintenanceServiceImpl implements AssetMaintenanceService {

    private static final Logger logger = LoggerFactory.getLogger(AssetMaintenanceServiceImpl.class);

    private final AssetMaintenanceRepository assetMaintenanceRepository;
    private final AssetReservationRepository assetReservationRepository;
    private final AssetDb assetDb;

    public AssetMaintenanceServiceImpl(AssetMaintenanceRepository assetMaintenanceRepository,
                                      AssetReservationRepository assetReservationRepository,
                                      AssetDb assetDb) {
        this.assetMaintenanceRepository = assetMaintenanceRepository;
        this.assetReservationRepository = assetReservationRepository;
        this.assetDb = assetDb;
    }    /**
     * Updates the status of an asset based on its maintenance status
     */
    private void updateAssetStatusForMaintenance(String platNomor, String status) {
        Asset asset = assetDb.findByIdAndNotDeleted(platNomor);
        if (asset == null) {
            throw new RuntimeException("Asset not found: " + platNomor);
        }

        if ("Sedang Berlangsung".equals(status)) {
            asset.setStatus("Dalam Maintenance");
        } else if ("Selesai".equals(status) || "Batal".equals(status)) {
            // Check if there are other active maintenances
            List<AssetMaintenance> activeMaintenance = assetMaintenanceRepository
                    .findActiveMaintenanceForAsset(platNomor);
            
            if (activeMaintenance.isEmpty()) {
                // No active maintenance, check for reservations
                List<AssetReservation> activeReservations = assetReservationRepository
                        .findActiveReservationsForAsset(platNomor);
                        
                if (activeReservations.isEmpty()) {
                    asset.setStatus("Tersedia");
                } else {
                    asset.setStatus("Tidak Tersedia");
                }
            }
        }

        assetDb.save(asset);
    }    @Override
    public AssetMaintenance createMaintenance(String platNomor, Date tanggalMulaiMaintenance, Date tanggalSelesaiMaintenance, 
                                             String deskripsiPekerjaan, Float biaya, String notes) {
        // Validate inputs
        if (platNomor == null || tanggalMulaiMaintenance == null || tanggalSelesaiMaintenance == null) {
            throw new IllegalArgumentException("Plat nomor, tanggal mulai, and tanggal selesai are required");
        }

        if (tanggalMulaiMaintenance.after(tanggalSelesaiMaintenance)) {
            throw new IllegalArgumentException("Tanggal mulai cannot be after tanggal selesai");
        }

        // Check if asset exists
        Asset asset = assetDb.findByIdAndNotDeleted(platNomor);
        if (asset == null) {
            throw new RuntimeException("Asset not found: " + platNomor);
        }

        // Check if asset is available for maintenance
        if (!isAssetAvailableForMaintenance(platNomor, tanggalMulaiMaintenance, tanggalSelesaiMaintenance)) {
            throw new RuntimeException("Asset is not available for maintenance during the specified period. " +
                    "It may be reserved for a project or under another maintenance.");
        }

        // Create the maintenance
        AssetMaintenance maintenance = new AssetMaintenance();
        maintenance.setPlatNomor(platNomor);
        maintenance.setTanggalMulaiMaintenance(tanggalMulaiMaintenance);
        maintenance.setTanggalSelesaiMaintenance(tanggalSelesaiMaintenance);
        maintenance.setStatus("Dijadwalkan");
        maintenance.setDeskripsiPekerjaan(deskripsiPekerjaan);
        maintenance.setBiaya(biaya);
        maintenance.setNotes(notes);
        maintenance.setCreatedDate(new Date());

        AssetMaintenance savedMaintenance = assetMaintenanceRepository.save(maintenance);

        // Update asset status if maintenance is starting soon or immediately
        Date now = new Date();
        if (tanggalMulaiMaintenance.before(now) || tanggalMulaiMaintenance.equals(now)) {
            updateAssetStatusForMaintenance(platNomor, "Sedang Berlangsung");
            savedMaintenance.setStatus("Sedang Berlangsung");
            savedMaintenance = assetMaintenanceRepository.save(savedMaintenance);
        }

        logger.info("Created maintenance for asset {} with ID {}", platNomor, savedMaintenance.getId());
        return savedMaintenance;
    }

    @Override
    public AssetMaintenance updateMaintenanceStatus(UUID maintenanceId, String maintenanceStatus) {
        Optional<AssetMaintenance> optionalMaintenance = assetMaintenanceRepository.findById(maintenanceId);

        if (optionalMaintenance.isEmpty()) {
            throw new RuntimeException("Maintenance not found with ID: " + maintenanceId);
        }

        AssetMaintenance maintenance = optionalMaintenance.get();
        String platNomor = maintenance.getPlatNomor();
        String oldStatus = maintenance.getMaintenanceStatus();

        // Validate status transition
        if (!isValidStatusTransition(oldStatus, maintenanceStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + oldStatus + " to " + maintenanceStatus);
        }

        maintenance.setMaintenanceStatus(maintenanceStatus);
        maintenance.setUpdatedDate(new Date());

        AssetMaintenance savedMaintenance = assetMaintenanceRepository.save(maintenance);

        // Update asset status
        updateAssetStatusForMaintenance(platNomor, maintenanceStatus);

        logger.info("Updated maintenance status for ID {} from {} to {}", maintenanceId, oldStatus, maintenanceStatus);
        return savedMaintenance;
    }

    @Override
    public AssetMaintenance updateMaintenance(UUID maintenanceId, String maintenanceType, Date startDate, 
                                             Date endDate, String description, Long cost, String technician, String notes) {
        Optional<AssetMaintenance> optionalMaintenance = assetMaintenanceRepository.findById(maintenanceId);

        if (optionalMaintenance.isEmpty()) {
            throw new RuntimeException("Maintenance not found with ID: " + maintenanceId);
        }

        AssetMaintenance maintenance = optionalMaintenance.get();

        // Only allow updates if maintenance is not completed or cancelled
        if ("Selesai".equals(maintenance.getMaintenanceStatus()) || "Batal".equals(maintenance.getMaintenanceStatus())) {
            throw new IllegalStateException("Cannot update completed or cancelled maintenance");
        }

        // If dates are being changed, check availability
        if (!startDate.equals(maintenance.getStartDate()) || !endDate.equals(maintenance.getEndDate())) {
            if (startDate.after(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }

            // Check availability excluding current maintenance
            if (!isAssetAvailableForMaintenanceExcludingCurrent(maintenance.getPlatNomor(), startDate, endDate, maintenanceId)) {
                throw new RuntimeException("Asset is not available for maintenance during the new specified period");
            }
        }

        // Update fields
        maintenance.setMaintenanceType(maintenanceType);
        maintenance.setStartDate(startDate);
        maintenance.setEndDate(endDate);
        maintenance.setDescription(description);
        maintenance.setCost(cost);
        maintenance.setTechnician(technician);
        maintenance.setNotes(notes);
        maintenance.setUpdatedDate(new Date());

        AssetMaintenance savedMaintenance = assetMaintenanceRepository.save(maintenance);

        logger.info("Updated maintenance with ID {}", maintenanceId);
        return savedMaintenance;
    }

    @Override
    public List<AssetMaintenance> getMaintenanceByAsset(String platNomor) {
        return assetMaintenanceRepository.findByPlatNomor(platNomor);
    }

    @Override
    public List<AssetMaintenance> getMaintenanceByStatus(String maintenanceStatus) {
        return assetMaintenanceRepository.findByMaintenanceStatus(maintenanceStatus);
    }

    @Override
    public boolean isAssetAvailableForMaintenance(String platNomor, Date startDate, Date endDate) {
        // Check for overlapping reservations
        List<AssetReservation> overlappingReservations = assetReservationRepository
                .findOverlappingReservations(platNomor, startDate, endDate);

        if (!overlappingReservations.isEmpty()) {
            logger.debug("Asset {} has overlapping reservations during maintenance period", platNomor);
            return false;
        }

        // Check for overlapping maintenance
        List<AssetMaintenance> overlappingMaintenance = assetMaintenanceRepository
                .findOverlappingMaintenance(platNomor, startDate, endDate);

        if (!overlappingMaintenance.isEmpty()) {
            logger.debug("Asset {} has overlapping maintenance during the specified period", platNomor);
            return false;
        }

        return true;
    }

    private boolean isAssetAvailableForMaintenanceExcludingCurrent(String platNomor, Date startDate, Date endDate, UUID excludeMaintenanceId) {
        // Check for overlapping reservations
        List<AssetReservation> overlappingReservations = assetReservationRepository
                .findOverlappingReservations(platNomor, startDate, endDate);

        if (!overlappingReservations.isEmpty()) {
            return false;
        }

        // Check for overlapping maintenance (excluding current one)
        List<AssetMaintenance> overlappingMaintenance = assetMaintenanceRepository
                .findOverlappingMaintenance(platNomor, startDate, endDate);

        overlappingMaintenance = overlappingMaintenance.stream()
                .filter(maintenance -> !maintenance.getId().equals(excludeMaintenanceId))
                .collect(Collectors.toList());

        return overlappingMaintenance.isEmpty();
    }

    @Override
    public Map<String, Boolean> checkAssetsAvailabilityForMaintenance(List<String> platNomors, Date startDate, Date endDate) {
        Map<String, Boolean> availability = new HashMap<>();

        for (String platNomor : platNomors) {
            boolean isAvailable = isAssetAvailableForMaintenance(platNomor, startDate, endDate);
            availability.put(platNomor, isAvailable);
        }

        return availability;
    }

    @Override
    public Map<String, String> getAssetAvailabilityDetails(List<String> platNomors, Date startDate, Date endDate) {
        Map<String, String> details = new HashMap<>();

        for (String platNomor : platNomors) {
            StringBuilder conflictDetails = new StringBuilder();

            // Check for overlapping reservations
            List<AssetReservation> overlappingReservations = assetReservationRepository
                    .findOverlappingReservations(platNomor, startDate, endDate);

            if (!overlappingReservations.isEmpty()) {
                conflictDetails.append("Tereservasi untuk proyek: ");
                overlappingReservations.forEach(reservation -> 
                    conflictDetails.append(reservation.getProjectId())
                        .append(" (")
                        .append(reservation.getStartDate())
                        .append(" - ")
                        .append(reservation.getEndDate())
                        .append("), "));
            }

            // Check for overlapping maintenance
            List<AssetMaintenance> overlappingMaintenance = assetMaintenanceRepository
                    .findOverlappingMaintenance(platNomor, startDate, endDate);

            if (!overlappingMaintenance.isEmpty()) {
                if (conflictDetails.length() > 0) {
                    conflictDetails.append(" | ");
                }
                conflictDetails.append("Sedang maintenance: ");
                overlappingMaintenance.forEach(maintenance -> 
                    conflictDetails.append(maintenance.getMaintenanceType())
                        .append(" (")
                        .append(maintenance.getStartDate())
                        .append(" - ")
                        .append(maintenance.getEndDate())
                        .append("), "));
            }

            if (conflictDetails.length() == 0) {
                details.put(platNomor, "Tersedia untuk maintenance");
            } else {
                // Remove trailing comma and space
                String finalDetails = conflictDetails.toString().replaceAll(", $", "");
                details.put(platNomor, finalDetails);
            }
        }

        return details;
    }

    @Override
    public List<AssetMaintenance> getMaintenanceByDateRange(Date startDate, Date endDate) {
        return assetMaintenanceRepository.findMaintenanceByDateRange(startDate, endDate);
    }

    @Override
    public AssetMaintenance cancelMaintenance(UUID maintenanceId) {
        Optional<AssetMaintenance> optionalMaintenance = assetMaintenanceRepository.findById(maintenanceId);

        if (optionalMaintenance.isEmpty()) {
            throw new RuntimeException("Maintenance not found with ID: " + maintenanceId);
        }

        AssetMaintenance maintenance = optionalMaintenance.get();

        if ("Selesai".equals(maintenance.getMaintenanceStatus()) || "Batal".equals(maintenance.getMaintenanceStatus())) {
            throw new IllegalStateException("Cannot cancel completed or already cancelled maintenance");
        }

        maintenance.setMaintenanceStatus("Batal");
        maintenance.setUpdatedDate(new Date());

        AssetMaintenance savedMaintenance = assetMaintenanceRepository.save(maintenance);

        // Update asset status
        updateAssetStatusForMaintenance(maintenance.getPlatNomor(), "Batal");

        logger.info("Cancelled maintenance with ID {}", maintenanceId);
        return savedMaintenance;
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        switch (currentStatus) {
            case "Dijadwalkan":
                return "Sedang Berlangsung".equals(newStatus) || "Batal".equals(newStatus);
            case "Sedang Berlangsung":
                return "Selesai".equals(newStatus) || "Batal".equals(newStatus);
            case "Selesai":
            case "Batal":
                return false; // Cannot change from these final states
            default:
                return false;
        }
    }
}
