package gpl.karina.resource.restdto.request;

import java.util.UUID;
import java.util.List;

import lombok.Data;

@Data
public class AddSupplierIdDTO {
    private UUID supplierId;
    private List<Long> resourceId;
}
