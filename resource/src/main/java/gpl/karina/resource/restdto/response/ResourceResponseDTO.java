package gpl.karina.resource.restdto.response;

import lombok.Data; 

@Data
public class ResourceResponseDTO {
    private Long id;
    private String resourceName;
    private String resourceDescription;
    private Integer resourceStock;
    private Integer resourcePrice;
}
