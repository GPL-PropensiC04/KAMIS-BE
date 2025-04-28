package gpl.karina.profile.restdto.response;

import lombok.Data;

import java.util.List;

@Data
public class DetailSupplierDTO {
    private String supplierName;
    private String supplierPhone;
    private String supplierEmail;
    private String supplierCompany;
    private String supplierAddress;
    private List<AssetDTO> assets;
    private List<PurchaseResponseDTO> purchases;
    private List<ResourceResponseDTO> resources;
}
