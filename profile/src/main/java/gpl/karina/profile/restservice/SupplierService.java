package gpl.karina.profile.restservice;

import gpl.karina.profile.restdto.request.AddSupplierRequestDTO;
import java.util.List;
import java.util.UUID;

import gpl.karina.profile.restdto.request.AddSupplierRequestDTO;
import gpl.karina.profile.restdto.request.UpdateSupplierRequestDTO;
import gpl.karina.profile.restdto.response.SupplierListResponseDTO;
import gpl.karina.profile.restdto.response.SupplierResponseDTO;

public interface SupplierService {
    SupplierResponseDTO addSupplier(AddSupplierRequestDTO addSupplierRequestDTO);
    List<SupplierResponseDTO> getAllSuppliers();
    List<SupplierListResponseDTO> filterSuppliers(String nameSupplier, String companySupplier);
    SupplierResponseDTO updateSupplier(UpdateSupplierRequestDTO dto);
    String getSupplierName(UUID supplierId);
    void addPurchaseId(UUID supplierId, String purchaseId);

}
