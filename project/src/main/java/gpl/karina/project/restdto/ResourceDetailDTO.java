package gpl.karina.project.restdto;

import lombok.Data;

@Data
public class ResourceDetailDTO {
    private Long id;
    private String resourceName;
    private Integer resourceStock;
    private Integer resourcePrice;
}
