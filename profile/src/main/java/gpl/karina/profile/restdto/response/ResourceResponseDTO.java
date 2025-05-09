package gpl.karina.profile.restdto.response;

import lombok.Data;

@Data
public class ResourceResponseDTO {
    private Long id;
    private String resourceName;
    private Integer resourcePrice;
}
