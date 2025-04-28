package gpl.karina.profile.restdto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PurchaseResponseDTO {
    private String purchaseId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date purchaseSubmissionDate;

    private String purchaseStatus;
    private String purchaseType;
    private String purchaseNote;
    private Integer purchasePrice;

    private String activityName; // << custom field yang kamu isi di service profile
}
