package gpl.karina.project.restdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceUsageDTO {
    private String resourceId;
    private Integer sellPrice;
    private Integer resourceStockUsed;
}