package gpl.karina.profile.restservice;

import gpl.karina.profile.restdto.request.AddSupplierRequestDTO;
import java.util.List;
import java.util.UUID;

import gpl.karina.profile.restdto.request.UpdateSupplierRequestDTO;
import gpl.karina.profile.restdto.response.DetailSupplierDTO;
import gpl.karina.profile.restdto.response.SupplierListResponseDTO;
import gpl.karina.profile.restdto.response.SupplierResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {
    SupplierResponseDTO addSupplier(AddSupplierRequestDTO addSupplierRequestDTO);

    List<SupplierResponseDTO> getAllSuppliers();

    Page<SupplierListResponseDTO> getAllSuppliersPaginated(Pageable pageable); // For paginated list

    List<SupplierListResponseDTO> filterSuppliers(String nameSupplier, String companySupplier);

    Page<SupplierListResponseDTO> filterSuppliersPaginated(String nameSupplier, String companySupplier,
            Pageable pageable);

    Page<SupplierListResponseDTO> getAllSupplierPaginated(Pageable pageable, String nameSupplier,
            String companySupplier);

    SupplierResponseDTO updateSupplier(UpdateSupplierRequestDTO dto);

    String getSupplierName(UUID supplierId);

    void addPurchaseId(UUID supplierId, String purchaseId);

    DetailSupplierDTO getSupplierDetail(UUID supplierId);

}
