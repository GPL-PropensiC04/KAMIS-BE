package gpl.karina.asset.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetReservationRequestDTO {
    private List<String> platNomors;
    private String projectId;
    private Date startDate;
    private Date endDate;
}