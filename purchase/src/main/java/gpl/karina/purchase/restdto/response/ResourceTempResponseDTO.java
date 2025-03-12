package gpl.karina.purchase.restdto.response;

import lombok.Data;

@Data
public class ResourceTempResponseDTO {
    private Long resourceId;
    private String resourceName;
    private Integer resourceTotal;
    private Integer resourcePrice;
}
