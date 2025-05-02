package gpl.karina.asset.controller;

import gpl.karina.asset.dto.request.AssetAvailabilityRequestDTO;
import gpl.karina.asset.dto.request.AssetReservationRequestDTO;
import gpl.karina.asset.dto.response.AssetReservationResponseDTO;
import gpl.karina.asset.dto.response.BaseResponseDTO;
import gpl.karina.asset.model.Asset;
import gpl.karina.asset.model.AssetReservation;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.service.AssetReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/asset/reservations")
public class AssetReservationController {

    private final AssetReservationService assetReservationService;
    private final AssetDb assetDb;

    public AssetReservationController(AssetReservationService assetReservationService, AssetDb assetDb) {
        this.assetReservationService = assetReservationService;
        this.assetDb = assetDb;
    }

    @PostMapping("/check-availability")
    public ResponseEntity<BaseResponseDTO<Map<String, Boolean>>> checkAssetsAvailability(
            @RequestBody AssetAvailabilityRequestDTO requestDTO) {

        BaseResponseDTO<Map<String, Boolean>> response = new BaseResponseDTO<>();

        try {
            Map<String, Boolean> availability;

            if (requestDTO.getExcludeProjectId() != null && !requestDTO.getExcludeProjectId().isEmpty()) {
                availability = assetReservationService.checkAssetsAvailabilityExcludingProject(
                        requestDTO.getPlatNomors(),
                        requestDTO.getStartDate(),
                        requestDTO.getEndDate(),
                        requestDTO.getExcludeProjectId());
            } else {
                availability = assetReservationService.checkAssetsAvailability(
                        requestDTO.getPlatNomors(),
                        requestDTO.getStartDate(),
                        requestDTO.getEndDate());
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Asset availability checked successfully");
            response.setData(availability);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reserve")
    public ResponseEntity<BaseResponseDTO<List<AssetReservationResponseDTO>>> reserveAssets(
            @RequestBody AssetReservationRequestDTO requestDTO) {

        BaseResponseDTO<List<AssetReservationResponseDTO>> response = new BaseResponseDTO<>();

        try {
            List<AssetReservation> reservations = assetReservationService.createReservations(
                    requestDTO.getPlatNomors(),
                    requestDTO.getProjectId(),
                    requestDTO.getStartDate(),
                    requestDTO.getEndDate());

            List<AssetReservationResponseDTO> responseDTOs = mapReservationsToResponseDTOs(reservations);

            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Assets reserved successfully");
            response.setData(responseDTOs);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/project/{projectId}/status")
    public ResponseEntity<BaseResponseDTO<List<AssetReservationResponseDTO>>> updateProjectReservationsStatus(
            @PathVariable(name = "projectId") String projectId,
            @RequestParam(name = "status") String status) {

        BaseResponseDTO<List<AssetReservationResponseDTO>> response = new BaseResponseDTO<>();

        try {
            List<AssetReservation> reservations = assetReservationService.updateProjectReservationStatus(projectId,
                    status);

            List<AssetReservationResponseDTO> responseDTOs = mapReservationsToResponseDTOs(reservations);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Reservations status updated successfully");
            response.setData(responseDTOs);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/asset/{platNomor}")
    public ResponseEntity<BaseResponseDTO<List<AssetReservationResponseDTO>>> getReservationsByAsset(
            @PathVariable(name = "platNomor") String platNomor) {

        BaseResponseDTO<List<AssetReservationResponseDTO>> response = new BaseResponseDTO<>();

        try {
            List<AssetReservation> reservations = assetReservationService.getReservationsByAsset(platNomor);

            List<AssetReservationResponseDTO> responseDTOs = mapReservationsToResponseDTOs(reservations);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Asset reservations retrieved successfully");
            response.setData(responseDTOs);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<BaseResponseDTO<List<AssetReservationResponseDTO>>> getReservationsByProject(
            @PathVariable(name = "projectId") String projectId) {

        BaseResponseDTO<List<AssetReservationResponseDTO>> response = new BaseResponseDTO<>();

        try {
            List<AssetReservation> reservations = assetReservationService.getReservationsByProject(projectId);

            List<AssetReservationResponseDTO> responseDTOs = mapReservationsToResponseDTOs(reservations);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Project reservations retrieved successfully");
            response.setData(responseDTOs);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{reservationId}/status")
    public ResponseEntity<BaseResponseDTO<AssetReservationResponseDTO>> updateReservationStatus(
            @PathVariable(name = "reservationId") UUID reservationId,
            @RequestParam(name = "status") String status) {

        BaseResponseDTO<AssetReservationResponseDTO> response = new BaseResponseDTO<>();

        try {
            AssetReservation reservation = assetReservationService.updateReservationStatus(reservationId, status);

            // Get the asset separately since lazy loading might not work in this context
            Asset asset = assetDb.findByIdAndNotDeleted(reservation.getPlatNomor());
            AssetReservationResponseDTO responseDTO = mapReservationToResponseDTO(reservation, asset);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Reservation status updated successfully");
            response.setData(responseDTO);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    private List<AssetReservationResponseDTO> mapReservationsToResponseDTOs(List<AssetReservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return new ArrayList<>();
        }

        return reservations.stream()
                .map(reservation -> {
                    // Get the asset separately since lazy loading might not work in this context
                    Asset asset = assetDb.findByIdAndNotDeleted(reservation.getPlatNomor());
                    return mapReservationToResponseDTO(reservation, asset);
                })
                .toList();
    }

    private AssetReservationResponseDTO mapReservationToResponseDTO(AssetReservation reservation, Asset asset) {
        AssetReservationResponseDTO dto = new AssetReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setPlatNomor(reservation.getPlatNomor());
        dto.setProjectId(reservation.getProjectId());
        dto.setStartDate(reservation.getStartDate());
        dto.setEndDate(reservation.getEndDate());
        dto.setReservationStatus(reservation.getReservationStatus());

        if (asset != null) {
            dto.setAssetName(asset.getNama());
            dto.setAssetType(asset.getJenisAset());
        }

        return dto;
    }
}