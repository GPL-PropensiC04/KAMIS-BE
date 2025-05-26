package gpl.karina.asset.controller;

import gpl.karina.asset.dto.request.MaintenanceRequestDTO;
import gpl.karina.asset.dto.response.BaseResponseDTO;
import gpl.karina.asset.dto.response.MaintenanceResponseDTO;
import gpl.karina.asset.service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;


    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    public ResponseEntity<BaseResponseDTO<MaintenanceResponseDTO>> createMaintenance(
            @Valid @RequestBody MaintenanceRequestDTO requestDTO, BindingResult bindingResult) {
        
        BaseResponseDTO<MaintenanceResponseDTO> response = new BaseResponseDTO<>();
        
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages.toString());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        try {
            MaintenanceResponseDTO result = maintenanceService.createMaintenance(requestDTO);
            
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Maintenance berhasil dicatat");
            response.setData(result);
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping ("/all")
    public ResponseEntity<BaseResponseDTO<List<MaintenanceResponseDTO>>> getAllMaintenance() {
        BaseResponseDTO<List<MaintenanceResponseDTO>> response = new BaseResponseDTO<>();
        
        List<MaintenanceResponseDTO> maintenanceList = maintenanceService.getAllMaintenance();
        
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Daftar maintenance berhasil diambil");
        response.setData(maintenanceList);
        response.setTimestamp(new Date());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<BaseResponseDTO<MaintenanceResponseDTO>> getMaintenanceById(@PathVariable Long id) {
    //     BaseResponseDTO<MaintenanceResponseDTO> response = new BaseResponseDTO<>();
        
    //     try {
    //         MaintenanceResponseDTO maintenance = maintenanceService.getMaintenanceById(id);
            
    //         response.setStatus(HttpStatus.OK.value());
    //         response.setMessage("Detail maintenance berhasil ditemukan");
    //         response.setData(maintenance);
    //         response.setTimestamp(new Date());
            
    //         return new ResponseEntity<>(response, HttpStatus.OK);
    //     } catch (Exception e) {
    //         response.setStatus(HttpStatus.NOT_FOUND.value());
    //         response.setMessage(e.getMessage());
    //         response.setTimestamp(new Date());
            
    //         return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    //     }
    // }

    @GetMapping("/{platNomor}")
    public ResponseEntity<BaseResponseDTO<List<MaintenanceResponseDTO>>> getMaintenanceByAsset(@PathVariable(name = "platNomor") String platNomor) {
        BaseResponseDTO<List<MaintenanceResponseDTO>> response = new BaseResponseDTO<>();
        
        try {
            List<MaintenanceResponseDTO> maintenanceList = maintenanceService.getMaintenanceByAssetId(platNomor);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar maintenance untuk asset berhasil diambil");
            response.setData(maintenanceList);
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<BaseResponseDTO<MaintenanceResponseDTO>> completeMaintenance(@PathVariable Long id) {
        BaseResponseDTO<MaintenanceResponseDTO> response = new BaseResponseDTO<>();
        
        try {
            MaintenanceResponseDTO maintenance = maintenanceService.completeMaintenance(id);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Maintenance berhasil diselesaikan");
            response.setData(maintenance);
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/maintenance-in-progress")
    public ResponseEntity<BaseResponseDTO<List<MaintenanceResponseDTO>>> getAssetsInMaintenance() {
        BaseResponseDTO<List<MaintenanceResponseDTO>> response = new BaseResponseDTO<>();

        try {
            // Memanggil service untuk mendapatkan daftar aset yang sedang dimaintenance
            List<MaintenanceResponseDTO> maintenanceList = maintenanceService.getAssetsInMaintenance();

            if (maintenanceList.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada aset yang sedang dalam maintenance");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar aset yang sedang dalam maintenance berhasil diambil");
            response.setData(maintenanceList);
            response.setTimestamp(new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan saat mengambil data aset yang sedang dalam maintenance: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}