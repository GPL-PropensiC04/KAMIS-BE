package gpl.karina.asset.controller;

import gpl.karina.asset.dto.request.AssetMaintenanceAvailabilityRequestDTO;
import gpl.karina.asset.dto.request.AssetMaintenanceRequestDTO;
import gpl.karina.asset.dto.response.AssetMaintenanceResponseDTO;
import gpl.karina.asset.dto.response.BaseResponseDTO;
import gpl.karina.asset.model.Asset;
import gpl.karina.asset.model.AssetMaintenance;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.service.AssetMaintenanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/asset/maintenance")
public class AssetMaintenanceController {

    private final AssetMaintenanceService assetMaintenanceService;
    private final AssetDb assetDb;

    public AssetMaintenanceController(AssetMaintenanceService assetMaintenanceService, AssetDb assetDb) {
        this.assetMaintenanceService = assetMaintenanceService;
        this.assetDb = assetDb;
    }

    @PostMapping("/check-availability")
    public ResponseEntity<BaseResponseDTO<Map<String, Object>>> checkAssetsAvailabilityForMaintenance(
            @RequestBody AssetMaintenanceAvailabilityRequestDTO requestDTO) {

        BaseResponseDTO<Map<String, Object>> response = new BaseResponseDTO<>();        try {
            Map<String, Boolean> availability = assetMaintenanceService
                    .checkAssetsAvailabilityForMaintenance(requestDTO.getPlatNomors(), 
                                                           requestDTO.getTanggalMulaiMaintenance(), 
                                                           requestDTO.getTanggalSelesaiMaintenance());

            Map<String, String> details = assetMaintenanceService
                    .getAssetAvailabilityDetails(requestDTO.getPlatNomors(), 
                                                 requestDTO.getTanggalMulaiMaintenance(), 
                                                 requestDTO.getTanggalSelesaiMaintenance());

            Map<String, Object> result = new HashMap<>();
            result.put("availability", availability);
            result.put("details", details);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Asset availability for maintenance checked successfully");
            response.setData(result);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/schedule")
    public ResponseEntity<BaseResponseDTO<AssetMaintenanceResponseDTO>> scheduleMaintenance(
            @RequestBody AssetMaintenanceRequestDTO requestDTO) {

        BaseResponseDTO<AssetMaintenanceResponseDTO> response = new BaseResponseDTO<>();        try {
            AssetMaintenance maintenance = assetMaintenanceService.createMaintenance(
                    requestDTO.getPlatNomor(),
                    requestDTO.getTanggalMulaiMaintenance(),
                    requestDTO.getTanggalSelesaiMaintenance(),
                    requestDTO.getDeskripsiPekerjaan(),
                    requestDTO.getBiaya(),
                    requestDTO.getNotes()
            );

            AssetMaintenanceResponseDTO responseDTO = mapMaintenanceToResponseDTO(maintenance);

            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Maintenance scheduled successfully");
            response.setData(responseDTO);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{maintenanceId}/status")
    public ResponseEntity<BaseResponseDTO<AssetMaintenanceResponseDTO>> updateMaintenanceStatus(
            @PathVariable(name = "maintenanceId") UUID maintenanceId,
            @RequestParam(name = "status") String status) {

        BaseResponseDTO<AssetMaintenanceResponseDTO> response = new BaseResponseDTO<>();

        try {
            AssetMaintenance maintenance = assetMaintenanceService.updateMaintenanceStatus(maintenanceId, status);
            AssetMaintenanceResponseDTO responseDTO = mapMaintenanceToResponseDTO(maintenance);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Maintenance status updated successfully");
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

    @PutMapping("/{maintenanceId}")
    public ResponseEntity<BaseResponseDTO<AssetMaintenanceResponseDTO>> updateMaintenance(
            @PathVariable(name = "maintenanceId") UUID maintenanceId,
            @RequestBody AssetMaintenanceRequestDTO requestDTO) {

        BaseResponseDTO<AssetMaintenanceResponseDTO> response = new BaseResponseDTO<>();        try {
            AssetMaintenance maintenance = assetMaintenanceService.updateMaintenance(
                    maintenanceId,
                    requestDTO.getTanggalMulaiMaintenance(),
                    requestDTO.getTanggalSelesaiMaintenance(),
                    requestDTO.getDeskripsiPekerjaan(),
                    requestDTO.getBiaya(),
                    requestDTO.getNotes()
            );

            AssetMaintenanceResponseDTO responseDTO = mapMaintenanceToResponseDTO(maintenance);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Maintenance updated successfully");
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

    @GetMapping("/asset/{platNomor}")
    public ResponseEntity<BaseResponseDTO<List<AssetMaintenanceResponseDTO>>> getMaintenanceByAsset(
            @PathVariable(name = "platNomor") String platNomor) {

        BaseResponseDTO<List<AssetMaintenanceResponseDTO>> response = new BaseResponseDTO<>();

        try {
            List<AssetMaintenance> maintenanceList = assetMaintenanceService.getMaintenanceByAsset(platNomor);
            List<AssetMaintenanceResponseDTO> responseDTOs = mapMaintenanceListToResponseDTOs(maintenanceList);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Maintenance records retrieved successfully");
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

    @GetMapping("/status/{status}")
    public ResponseEntity<BaseResponseDTO<List<AssetMaintenanceResponseDTO>>> getMaintenanceByStatus(
            @PathVariable(name = "status") String status) {

        BaseResponseDTO<List<AssetMaintenanceResponseDTO>> response = new BaseResponseDTO<>();

        try {
            List<AssetMaintenance> maintenanceList = assetMaintenanceService.getMaintenanceByStatus(status);
            List<AssetMaintenanceResponseDTO> responseDTOs = mapMaintenanceListToResponseDTOs(maintenanceList);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Maintenance records retrieved successfully");
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

    @GetMapping("/schedule")
    public ResponseEntity<BaseResponseDTO<List<AssetMaintenanceResponseDTO>>> getMaintenanceByDateRange(
            @RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        BaseResponseDTO<List<AssetMaintenanceResponseDTO>> response = new BaseResponseDTO<>();

        try {
            List<AssetMaintenance> maintenanceList = assetMaintenanceService.getMaintenanceByDateRange(startDate, endDate);
            List<AssetMaintenanceResponseDTO> responseDTOs = mapMaintenanceListToResponseDTOs(maintenanceList);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Maintenance schedule retrieved successfully");
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

    @DeleteMapping("/{maintenanceId}")
    public ResponseEntity<BaseResponseDTO<AssetMaintenanceResponseDTO>> cancelMaintenance(
            @PathVariable(name = "maintenanceId") UUID maintenanceId) {

        BaseResponseDTO<AssetMaintenanceResponseDTO> response = new BaseResponseDTO<>();

        try {
            AssetMaintenance maintenance = assetMaintenanceService.cancelMaintenance(maintenanceId);
            AssetMaintenanceResponseDTO responseDTO = mapMaintenanceToResponseDTO(maintenance);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Maintenance cancelled successfully");
            response.setData(responseDTO);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }    private List<AssetMaintenanceResponseDTO> mapMaintenanceListToResponseDTOs(List<AssetMaintenance> maintenanceList) {
        if (maintenanceList == null || maintenanceList.isEmpty()) {
            return new ArrayList<>();
        }

        return maintenanceList.stream()
                .map(this::mapMaintenanceToResponseDTO)
                .collect(Collectors.toList());
    }

    private AssetMaintenanceResponseDTO mapMaintenanceToResponseDTO(AssetMaintenance maintenance) {
        AssetMaintenanceResponseDTO dto = new AssetMaintenanceResponseDTO();
        dto.setId(maintenance.getId());
        dto.setPlatNomor(maintenance.getPlatNomor());
        dto.setTanggalMulaiMaintenance(maintenance.getTanggalMulaiMaintenance());
        dto.setTanggalSelesaiMaintenance(maintenance.getTanggalSelesaiMaintenance());
        dto.setStatus(maintenance.getStatus());
        dto.setDeskripsiPekerjaan(maintenance.getDeskripsiPekerjaan());
        dto.setBiaya(maintenance.getBiaya());
        dto.setNotes(maintenance.getNotes());
        dto.setCreatedDate(maintenance.getCreatedDate());
        dto.setUpdatedDate(maintenance.getUpdatedDate());

        // Get asset details for additional information
        Asset asset = assetDb.findByIdAndNotDeleted(maintenance.getPlatNomor());
        if (asset != null) {
            dto.setAssetName(asset.getNama());
            dto.setAssetType(asset.getJenisAset());
        }

        // Check for conflicts
        Map<String, String> conflictDetails = assetMaintenanceService
                .getAssetAvailabilityDetails(Arrays.asList(maintenance.getPlatNomor()), 
                                           maintenance.getTanggalMulaiMaintenance(), 
                                           maintenance.getTanggalSelesaiMaintenance());
        
        String conflictInfo = conflictDetails.get(maintenance.getPlatNomor());
        dto.setHasConflictWithReservation(!conflictInfo.equals("Tersedia untuk maintenance"));
        dto.setConflictDetails(conflictInfo);

        return dto;
    }
}
