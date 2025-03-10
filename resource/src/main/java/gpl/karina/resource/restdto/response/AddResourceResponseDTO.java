package gpl.karina.resource.restdto.response;

import lombok.Data; 

@Data
public class AddResourceResponseDTO {
    private String resourceName;
    private String resourceDescription;
    private String resourceSupplier;
    private Integer resourceStock;
    private Integer resourcePrice;
}
