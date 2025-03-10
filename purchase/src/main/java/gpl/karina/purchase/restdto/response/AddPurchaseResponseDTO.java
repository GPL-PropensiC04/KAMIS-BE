package gpl.karina.purchase.restdto.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class AddPurchaseResponseDTO { //TODO: Belum add attribute foto
    private String purchaseId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date purchaseSubmissionDate;
    private String purchaseSupplier;
    private String purchaseType;
    private List<ResourceTempResponseDTO> purchaseResource;
    private String assetType;
    private String assetDescription;
    private Integer purchasePrice;
}
