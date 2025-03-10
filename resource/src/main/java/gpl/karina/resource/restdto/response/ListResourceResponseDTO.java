package gpl.karina.resource.restdto.response;

import lombok.Data; 

@Data
public class ListResourceResponseDTO {
    private String resourceName;
    private String resourceDescription;
    private Integer resourceStock;
    private Integer resourcePrice;
}
