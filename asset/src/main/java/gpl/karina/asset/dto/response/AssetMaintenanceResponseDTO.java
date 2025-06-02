package gpl.karina.asset.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetMaintenanceResponseDTO {
    private UUID id;
    private String platNomor;
    private Date tanggalMulaiMaintenance;
    private Date tanggalSelesaiMaintenance;
    private String status;
    private String deskripsiPekerjaan;
    private Float biaya;
    private String notes;
    private Date createdDate;
    private Date updatedDate;

    // Additional fields for client convenience
    private String assetName;
    private String assetType;
    
    // Conflict information
    private boolean hasConflictWithReservation;
    private String conflictDetails;
}
