package gpl.karina.profile.restdto.response;

import java.util.UUID;

import lombok.Data;

@Data
public class ClientListResponseDTO {
    private UUID id;
    private String nameClient;
    private String typeClient;
    private String companyClient;
    private Integer projectCount;
    private Long totalProfit;
}
