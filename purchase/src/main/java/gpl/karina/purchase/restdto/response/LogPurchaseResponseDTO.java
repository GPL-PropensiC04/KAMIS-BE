package gpl.karina.purchase.restdto.response;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class LogPurchaseResponseDTO {
    private UUID id;
    private String user;
    private String action;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date actionDate;
}
