package gpl.karina.asset.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetReservationResponseDTO {
    private UUID id;
    private String platNomor;
    private String projectId;
    private Date startDate;
    private Date endDate;
    private String reservationStatus;

    // Additional fields for client convenience
    private String assetName;
    private String assetType;
}