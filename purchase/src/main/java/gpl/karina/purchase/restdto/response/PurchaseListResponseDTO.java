package gpl.karina.purchase.restdto.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PurchaseListResponseDTO {
    private String purchaseId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date purchaseSubmissionDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date purchaseUpdateDate;
    private String purchaseSupplier;
    private String purchaseType;
    private String purchaseStatus;
    private Integer purchasePrice;
}
