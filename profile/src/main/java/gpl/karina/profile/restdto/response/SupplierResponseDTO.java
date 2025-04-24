package gpl.karina.profile.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {
    private UUID id;
    private String nameSupplier;
    private String noTelpSupplier;
    private String emailSupplier;
    private String companySupplier;
    private String addressSupplier;
    private List<Long> resourceIds;
    private List<String> assetIds; // List of asset IDs from Asset Service
    private List<String> purchaseIds;
    private Date createdDate;
    private Date updatedDate;


}
