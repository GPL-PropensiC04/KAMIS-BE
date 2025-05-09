package gpl.karina.purchase.restdto.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PurchaseResponseDTO {
    private String purchaseId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date purchaseSubmissionDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date purchaseUpdateDate;
    private UUID purchaseSupplier;
    private String purchaseType;
    private String purchaseStatus;
    private List<ResourceTempResponseDTO> purchaseResource;
    private AssetTempResponseDTO purchaseAsset;
    private Integer purchasePrice;
    private String purchaseNote;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date purchasePaymentDate;
    private List<LogPurchaseResponseDTO> purchaseLogs;
}
