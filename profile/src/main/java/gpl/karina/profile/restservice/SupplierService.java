package gpl.karina.profile.restservice;

import gpl.karina.profile.restdto.request.AddSupplierRequestDTO;
import gpl.karina.profile.restdto.response.SupplierResponseDTO;

public interface SupplierService {
    SupplierResponseDTO addSupplier(AddSupplierRequestDTO addSupplierRequestDTO);
}
