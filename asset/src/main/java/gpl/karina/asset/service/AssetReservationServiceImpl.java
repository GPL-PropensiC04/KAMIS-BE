package gpl.karina.asset.service;

import gpl.karina.asset.model.Asset;
import gpl.karina.asset.model.AssetReservation;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.repository.AssetReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetReservationServiceImpl implements AssetReservationService {

    private static final Logger logger = LoggerFactory.getLogger(AssetReservationServiceImpl.class);

    private final AssetReservationRepository assetReservationRepository;
    private final AssetDb assetDb;
    private final AssetService assetService;

    public AssetReservationServiceImpl(AssetReservationRepository assetReservationRepository, AssetDb assetDb,
            AssetService assetService) {
        this.assetReservationRepository = assetReservationRepository;
        this.assetDb = assetDb;
        this.assetService = assetService;
    }

    /**
     * Updates the status of an asset based on its reservations
     */
    private void updateAssetStatus(String platNomor, String reservationStatus) {
        Asset asset = assetDb.findByIdAndNotDeleted(platNomor);
        if (asset == null) {
            logger.warn("Attempted to update status for non-existent asset: {}", platNomor);
            return;
        }

        if ("Dilaksanakan".equals(reservationStatus)) {
            // Asset is now in use by a project
            asset.setStatus("Dalam Aktivitas");
            logger.info("Asset {} status updated to: Dalam Aktivitas", platNomor);
        } else if ("Selesai".equals(reservationStatus) || "Batal".equals(reservationStatus)) {
            // Check if there are other active reservations for this asset
            List<AssetReservation> activeReservations = assetReservationRepository
                    .findActiveReservationsForAsset(platNomor);

            if (activeReservations.isEmpty()) {
                // No active reservations, asset is available
                asset.setStatus("Tersedia");
                logger.info("Asset {} status updated to: Tersedia", platNomor);
            } else {
                // Asset still has active reservations
                logger.info("Asset {} still has {} active reservations", platNomor, activeReservations.size());
            }
        }

        assetDb.save(asset);
    }

    @Override
    public AssetReservation createReservation(String platNomor, String projectId, Date startDate, Date endDate) {
        // Validate inputs
        if (platNomor == null || projectId == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Required parameters cannot be null");
        }

        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Check if asset exists
        Asset asset = assetDb.findByIdAndNotDeleted(platNomor);
        if (asset == null) {
            throw new IllegalArgumentException("Asset with plate number " + platNomor + " not found");
        }

        // Check if asset is available for this period
        if (!isAssetAvailable(platNomor, startDate, endDate)) {
            throw new IllegalArgumentException("Asset is not available for this time period");
        }

        // Create the reservation
        AssetReservation reservation = new AssetReservation();
        reservation.setPlatNomor(platNomor);
        reservation.setProjectId(projectId);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setReservationStatus("Direncanakan");

        AssetReservation savedReservation = assetReservationRepository.save(reservation);

        // Update asset status upon creating reservation
        updateAssetStatus(platNomor, "Direncanakan");

        return savedReservation;
    }

    @Override
    public List<AssetReservation> createReservations(List<String> platNomors, String projectId, Date startDate,
            Date endDate) {
        if (platNomors == null || platNomors.isEmpty() || projectId == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Required parameters cannot be null or empty");
        }

        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Check availability of all assets first
        Map<String, Boolean> availability = checkAssetsAvailability(platNomors, startDate, endDate);
        List<String> unavailableAssets = availability.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!unavailableAssets.isEmpty()) {
            throw new IllegalArgumentException(
                    "The following assets are not available: " + String.join(", ", unavailableAssets));
        }

        // Create reservations
        List<AssetReservation> reservations = new ArrayList<>();
        for (String platNomor : platNomors) {
            AssetReservation reservation = new AssetReservation();
            reservation.setPlatNomor(platNomor);
            reservation.setProjectId(projectId);
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setReservationStatus("Direncanakan");
            reservations.add(reservation);
        }

        List<AssetReservation> savedReservations = assetReservationRepository.saveAll(reservations);

        // Update asset statuses after creating reservations
        for (String platNomor : platNomors) {
            updateAssetStatus(platNomor, "Direncanakan");
        }

        return savedReservations;
    }

    @Override
    public AssetReservation updateReservationStatus(UUID reservationId, String reservationStatus) {
        Optional<AssetReservation> optionalReservation = assetReservationRepository.findById(reservationId);

        if (optionalReservation.isEmpty()) {
            throw new IllegalArgumentException("Reservation not found");
        }

        AssetReservation reservation = optionalReservation.get();
        String platNomor = reservation.getPlatNomor();
        reservation.setReservationStatus(reservationStatus);

        AssetReservation updatedReservation = assetReservationRepository.save(reservation);

        // Update asset status after reservation status change
        updateAssetStatus(platNomor, reservationStatus);

        return updatedReservation;
    }

    @Override
    public List<AssetReservation> updateProjectReservationStatus(String projectId, String reservationStatus) {
        List<AssetReservation> projectReservations = assetReservationRepository.findByProjectId(projectId);

        if (projectReservations.isEmpty()) {
            return Collections.emptyList();
        }

        // Store plat nomors before updating
        Set<String> affectedAssets = projectReservations.stream()
                .map(AssetReservation::getPlatNomor)
                .collect(Collectors.toSet());

        for (AssetReservation reservation : projectReservations) {
            reservation.setReservationStatus(reservationStatus);
        }

        List<AssetReservation> updatedReservations = assetReservationRepository.saveAll(projectReservations);

        // Update all affected asset statuses
        for (String platNomor : affectedAssets) {
            updateAssetStatus(platNomor, reservationStatus);
        }

        return updatedReservations;
    }

    @Override
    public List<AssetReservation> getReservationsByAsset(String platNomor) {
        return assetReservationRepository.findByPlatNomor(platNomor);
    }

    @Override
    public List<AssetReservation> getReservationsByProject(String projectId) {
        return assetReservationRepository.findByProjectId(projectId);
    }

    @Override
    public boolean isAssetAvailable(String platNomor, Date startDate, Date endDate) {
        // Check if asset exists
        Asset asset = assetDb.findByIdAndNotDeleted(platNomor);
        if (asset == null) {
            throw new IllegalArgumentException("Asset with plate number " + platNomor + " not found");
        }

        // Check if asset is under maintenance
        if ("Sedang Maintenance".equals(asset.getStatus())) {
            logger.info("Asset {} is currently under maintenance and cannot be reserved.", platNomor);
            return false;
        }
        

        // Check for any overlapping reservations in the given time period
        List<AssetReservation> overlappingReservations = assetReservationRepository
                .findOverlappingReservations(platNomor, startDate, endDate);

        return overlappingReservations.isEmpty();
    }

    @Override
    public boolean isAssetAvailableExcludingProject(String platNomor, Date startDate, Date endDate,
            String excludeProjectId) {
        // Check if asset exists
        Asset asset = assetDb.findByIdAndNotDeleted(platNomor);
        if (asset == null) {
            throw new IllegalArgumentException("Asset with plate number " + platNomor + " not found");
        }

        // Check if asset is under maintenance
        if ("Sedang Maintenance".equals(asset.getStatus())) {
            logger.info("Asset {} is currently under maintenance and cannot be reserved.", platNomor);
            return false;
        }
        

        // Check for any overlapping reservations in the given time period, excluding
        // the specified project's reservations
        List<AssetReservation> overlappingReservations = assetReservationRepository
                .findOverlappingReservationsExcludingProject(platNomor, startDate, endDate, excludeProjectId);

        return overlappingReservations.isEmpty();
    }

    @Override
    public Map<String, Boolean> checkAssetsAvailability(List<String> platNomors, Date startDate, Date endDate) {
        Map<String, Boolean> availabilityMap = new HashMap<>();

        for (String platNomor : platNomors) {
            try {
                boolean isAvailable = isAssetAvailable(platNomor, startDate, endDate);
                availabilityMap.put(platNomor, isAvailable);
            } catch (IllegalArgumentException e) {
                // Asset not found or other validation errors
                availabilityMap.put(platNomor, false);
                logger.warn("Error checking availability for asset {}: {}", platNomor, e.getMessage());
            }
        }

        return availabilityMap;
    }

    @Override
    public Map<String, Boolean> checkAssetsAvailabilityExcludingProject(List<String> platNomors, Date startDate,
            Date endDate, String excludeProjectId) {
        Map<String, Boolean> availabilityMap = new HashMap<>();

        for (String platNomor : platNomors) {
            try {
                boolean isAvailable = isAssetAvailableExcludingProject(platNomor, startDate, endDate, excludeProjectId);
                availabilityMap.put(platNomor, isAvailable);
            } catch (IllegalArgumentException e) {
                // Asset not found or other validation errors
                availabilityMap.put(platNomor, false);
                logger.warn("Error checking availability for asset {}: {}", platNomor, e.getMessage());
            }
        }

        return availabilityMap;
    }
}