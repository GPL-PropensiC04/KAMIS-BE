package gpl.karina.asset.service;

import gpl.karina.asset.dto.request.MaintenanceRequestDTO;
import gpl.karina.asset.dto.response.MaintenanceResponseDTO;

import java.util.List;

public interface MaintenanceService {
    MaintenanceResponseDTO createMaintenance(MaintenanceRequestDTO requestDTO) throws Exception;
    List<MaintenanceResponseDTO> getAllMaintenance();
    List<MaintenanceResponseDTO> getMaintenanceByAssetId(String platNomor) throws Exception;
    MaintenanceResponseDTO getMaintenanceById(Long id) throws Exception;
    MaintenanceResponseDTO completeMaintenance(Long id) throws Exception;
}