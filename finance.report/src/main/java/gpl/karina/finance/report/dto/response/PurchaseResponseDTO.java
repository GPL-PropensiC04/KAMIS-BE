package gpl.karina.finance.report.dto.response;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PurchaseResponseDTO {
    private String purchaseId;
    private String purchaseType;
    private String purchaseStatus;
    private Integer purchasePrice;
    private List<ResourceTempResponseDTO> purchaseResource;
    private AssetTempResponseDTO purchaseAsset;
    private Date purchasePaymentDate;
}
