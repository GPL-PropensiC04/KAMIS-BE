package gpl.karina.profile.restdto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class SupplierListResponseDTO {
    private UUID id;
    private String nameSupplier;
    private String companySupplier;
    private Integer totalPurchases; // untuk sekarang bisa null atau dummy
}
