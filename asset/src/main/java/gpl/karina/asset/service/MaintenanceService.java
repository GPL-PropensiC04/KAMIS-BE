package gpl.karina.asset.service;

import gpl.karina.asset.dto.request.MaintenanceRequestDTO;
import gpl.karina.asset.dto.response.MaintenanceResponseDTO;
import java.util.List;

public interface MaintenanceService {
    
    MaintenanceResponseDTO createMaintenance(MaintenanceRequestDTO requestDTO) throws Exception;
    
    List<MaintenanceResponseDTO> getAllMaintenance();
    
    MaintenanceResponseDTO getMaintenanceById(Long id) throws Exception;
    
    MaintenanceResponseDTO completeMaintenance(Long id) throws Exception;
    
    List<MaintenanceResponseDTO> getMaintenanceByAssetId(String platNomor) throws Exception;
    
    // Add this method to fix the compilation error
    List<MaintenanceResponseDTO> getMaintenanceByAssetIdAndStatus(String platNomor, String status) throws Exception;
}