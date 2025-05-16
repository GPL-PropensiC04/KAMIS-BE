package gpl.karina.finance.report.dto.response;

import lombok.Data;

@Data
public class PurchaseResponseDTO {
    private String purchaseId;
    private String purchaseType;
    private String purchaseStatus;
    private Integer purchasePrice;
    private Boolean projectType; 
}
