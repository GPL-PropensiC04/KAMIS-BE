package gpl.karina.profile.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.profile.model.Supplier;
import gpl.karina.profile.repository.SupplierRepository;
import gpl.karina.profile.restdto.request.AddSupplierRequestDTO;
import gpl.karina.profile.restdto.request.UpdateSupplierRequestDTO;
import gpl.karina.profile.restdto.response.BaseResponseDTO;
import gpl.karina.profile.restdto.response.ResourceResponseDTO;
import gpl.karina.profile.restdto.response.SupplierListResponseDTO;
import gpl.karina.profile.restdto.response.SupplierResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final HttpServletRequest request;

    @Value("${profile.app.resourceUrl}")
    private String resourceUrl;

    private final WebClient webClientResource = WebClient.create();


    public SupplierServiceImpl(SupplierRepository supplierRepository, HttpServletRequest request) {
        this.supplierRepository = supplierRepository;
        this.request = request;

    }
    
    public String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private List<ResourceResponseDTO> fetchAllResources() {
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }
    
        String url = resourceUrl + "api/resource/viewall";
    
        try {
            BaseResponseDTO<List<ResourceResponseDTO>> response = webClientResource
                .get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> {
                    if (res.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        System.out.println("Tidak ada resource ditemukan.");
                        return Mono.empty();
                    }
                    return Mono.error(new RuntimeException("Client error: " + res.statusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, res -> {
                    System.err.println("Server error saat mengambil resource: " + res.statusCode());
                    return Mono.error(new RuntimeException("Server error: " + res.statusCode()));
                })
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<ResourceResponseDTO>>>() {})
                .block();
    
            if (response == null || response.getData() == null) {
                return new ArrayList<>();
            }
            return response.getData();
    
        } catch (Exception e) {
            System.err.println("Exception saat ambil resource: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private SupplierResponseDTO supplierToSupplierResponseDTO(Supplier supplier) {
        SupplierResponseDTO response = new SupplierResponseDTO();
        response.setId(supplier.getId());
        response.setNameSupplier(supplier.getNameSupplier());
        response.setNoTelpSupplier(supplier.getNoTelpSupplier());
        response.setEmailSupplier(supplier.getEmailSupplier());
        response.setCompanySupplier(supplier.getCompanySupplier());
        response.setAddressSupplier(supplier.getAddressSupplier());
        response.setResourceIds(supplier.getResourceIds());
        response.setAssetIds(supplier.getAssetIds());
        response.setPurchaseIds(supplier.getPurchaseIds());
        response.setCreatedDate(supplier.getCreatedDate());
        response.setUpdatedDate(supplier.getUpdatedDate());
        return response;
    }

    @Override
    public SupplierResponseDTO addSupplier(AddSupplierRequestDTO dto) {
        // Validasi nomor telepon: hanya angka
        if (!dto.getNoTelpSupplier().matches("\\d+")) {
            throw new IllegalArgumentException("Nomor telepon hanya boleh terdiri dari angka.");
        }
    
        // Validasi unik
        if (supplierRepository.existsByNameSupplier(dto.getNameSupplier())) {
            throw new IllegalArgumentException("Nama supplier sudah digunakan.");
        }
        if (supplierRepository.existsByNoTelpSupplier(dto.getNoTelpSupplier())) {
            throw new IllegalArgumentException("Nomor telepon sudah digunakan oleh supplier lain.");
        }
        if (supplierRepository.existsByEmailSupplier(dto.getEmailSupplier())) {
            throw new IllegalArgumentException("Email sudah digunakan oleh supplier lain.");
        }
        if (supplierRepository.existsByCompanySupplier(dto.getCompanySupplier())) {
            throw new IllegalArgumentException("Nama perusahaan sudah digunakan oleh supplier lain.");
        }
    
        // Validasi resourceIds jika ada
        List<Long> resourceIds = dto.getResourceIds() != null ? dto.getResourceIds() : new ArrayList<>();
        if (!resourceIds.isEmpty()) {
            List<Long> validResourceIds = fetchAllResources().stream()
                                            .map(ResourceResponseDTO::getId)
                                            .toList();
    
            for (Long id : resourceIds) {
                if (!validResourceIds.contains(id)) {
                    throw new IllegalArgumentException("Resource ID tidak valid: " + id);
                }
            }
        }
    
        Supplier supplier = new Supplier();
        supplier.setNameSupplier(dto.getNameSupplier());
        supplier.setNoTelpSupplier(dto.getNoTelpSupplier());
        supplier.setEmailSupplier(dto.getEmailSupplier());
        supplier.setCompanySupplier(dto.getCompanySupplier());
        supplier.setAddressSupplier(dto.getAddressSupplier());
        supplier.setResourceIds(resourceIds);
        supplier.setCreatedDate(new Date());
        supplier.setUpdatedDate(new Date());
    
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierToSupplierResponseDTO(savedSupplier);
    }
    
    @Override
    public List<SupplierListResponseDTO> filterSuppliers(String nameSupplier, String companySupplier) {
        return supplierRepository.findAll().stream()
                .filter(supplier -> {
                    boolean matches = true;
                    if (nameSupplier != null) {
                        matches &= supplier.getNameSupplier().toLowerCase().contains(nameSupplier.toLowerCase());
                    }
                    if (companySupplier != null) {
                        matches &= supplier.getCompanySupplier().toLowerCase().contains(companySupplier.toLowerCase());
                    }
                    return matches;
                })
                .map(this::supplierToSupplierListResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::supplierToSupplierResponseDTO)
                .collect(Collectors.toList());
    }

    private SupplierListResponseDTO supplierToSupplierListResponseDTO(Supplier supplier) {
        SupplierListResponseDTO dto = new SupplierListResponseDTO();
        dto.setId(supplier.getId());
        dto.setNameSupplier(supplier.getNameSupplier());
        dto.setCompanySupplier(supplier.getCompanySupplier());
        dto.setTotalPurchases(0); // placeholder
        return dto;
    }

    @Override
    public SupplierResponseDTO updateSupplier(UpdateSupplierRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier tidak ditemukan."));

        // Validasi nomor telepon
        if (dto.getNoTelpSupplier() != null && !dto.getNoTelpSupplier().matches("\\d+")) {
            throw new IllegalArgumentException("Nomor telepon hanya boleh terdiri dari angka.");
        }

        // Validasi unik (jika ingin dihindari data duplikat selain supplier ini sendiri)
        if (dto.getNoTelpSupplier() != null &&
            supplierRepository.existsByNoTelpSupplierAndIdNot(dto.getNoTelpSupplier(), dto.getId())) {
            throw new IllegalArgumentException("Nomor telepon sudah digunakan.");
        }
        if (dto.getEmailSupplier() != null &&
            supplierRepository.existsByEmailSupplierAndIdNot(dto.getEmailSupplier(), dto.getId())) {
            throw new IllegalArgumentException("Email sudah digunakan.");
        }

        if (supplierRepository.existsByNameSupplierAndIdNot(dto.getNameSupplier(), dto.getId())) {
            throw new IllegalArgumentException("Nama supplier sudah digunakan.");
        }

        if (dto.getNameSupplier() != null && supplierRepository.existsByNameSupplier(dto.getNameSupplier())) {
            throw new IllegalArgumentException("Nama supplier sudah digunakan.");
        }

        // Validasi resourceIds
        List<Long> resourceIds = dto.getResourceIds() != null ? dto.getResourceIds() : new ArrayList<>();
        if (!resourceIds.isEmpty()) {
            List<Long> validResourceIds = fetchAllResources().stream()
                    .map(ResourceResponseDTO::getId)
                    .toList();

            for (Long id : resourceIds) {
                if (!validResourceIds.contains(id)) {
                    throw new IllegalArgumentException("Resource ID tidak valid: " + id);
                }
            }
        }

        // Update field (selain company name)
        if (dto.getAddressSupplier() != null) supplier.setAddressSupplier(dto.getAddressSupplier());
        if (dto.getNoTelpSupplier() != null) supplier.setNoTelpSupplier(dto.getNoTelpSupplier());
        if (dto.getEmailSupplier() != null) supplier.setEmailSupplier(dto.getEmailSupplier());
        if (dto.getNameSupplier() != null) supplier.setNameSupplier(dto.getNameSupplier());
        supplier.setResourceIds(resourceIds);
        supplier.setUpdatedDate(new Date());

        Supplier saved = supplierRepository.save(supplier);
        return supplierToSupplierResponseDTO(saved);
    }


}
