package gpl.karina.profile.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Date;
import java.util.stream.Collectors;
import java.text.NumberFormat;

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
import gpl.karina.profile.restdto.response.AssetDTO;
import gpl.karina.profile.restdto.response.BaseResponseDTO;
import gpl.karina.profile.restdto.response.DetailSupplierDTO;
import gpl.karina.profile.restdto.response.ResourceResponseDTO;
import gpl.karina.profile.restdto.response.SupplierResponseDTO;
import gpl.karina.profile.restdto.request.UpdateSupplierRequestDTO;
import gpl.karina.profile.restdto.response.BaseResponseDTO;
import gpl.karina.profile.restdto.response.ResourceResponseDTO;
import gpl.karina.profile.restdto.response.SupplierListResponseDTO;
import gpl.karina.profile.restdto.response.SupplierResponseDTO;
import gpl.karina.profile.restdto.request.AddPurchaseIdDTO;
import gpl.karina.profile.restdto.request.AddSupplierIdDTO;
import gpl.karina.profile.restdto.response.PurchaseResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final HttpServletRequest request;

    @Value("${profile.app.assetUrl}")
    private String assetUrl;

    @Value("${profile.app.resourceUrl}")
    private String resourceUrl;

    @Value("${profile.app.purchaseUrl}")
    private String purchaseUrl;

    private final WebClient webClient = WebClient.create();


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

    private void addSupplierIdToResources(UUID supplierId, List<Long> resourceIds) {
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }

        String url = resourceUrl + "api/resource/add-supplier";

        AddSupplierIdDTO requestDTO = new AddSupplierIdDTO();
        requestDTO.setSupplierId(supplierId);
        requestDTO.setResourceId(resourceIds);

        webClient
            .put()
            .uri(url)
            .headers(headers -> headers.setBearerAuth(token))
            .bodyValue(requestDTO)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    private void updateSupplierIdInResources(UUID supplierId, List<Long> resourceIds) {
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }

        String url = resourceUrl + "api/resource/update-supplier";

        AddSupplierIdDTO requestDTO = new AddSupplierIdDTO();
        requestDTO.setSupplierId(supplierId);
        requestDTO.setResourceId(resourceIds);

        webClient
            .put()
            .uri(url)
            .headers(headers -> headers.setBearerAuth(token))
            .bodyValue(requestDTO)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }

    private List<ResourceResponseDTO> fetchAllResources() {
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }
    
        String url = resourceUrl + "api/resource/viewall";
    
        try {
            BaseResponseDTO<List<ResourceResponseDTO>> response = webClient
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

    private Integer fetchTotalPurchasesBySupplier(UUID supplierId) {
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }
    
        String url = purchaseUrl + "api/purchase/supplier/" + supplierId; // contoh path baru yang lebih general
    
        try {
            BaseResponseDTO<List<PurchaseResponseDTO>> response = webClient
                .get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> {
                    if (res.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        System.out.println("Purchases tidak ditemukan untuk supplier ID: " + supplierId);
                        return Mono.empty();
                    }
                    return Mono.error(new RuntimeException("Client error: " + res.statusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, res -> {
                    System.err.println("Server error saat ambil purchases: " + res.statusCode());
                    return Mono.error(new RuntimeException("Server error: " + res.statusCode()));
                })
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<PurchaseResponseDTO>>>() {})
                .block();
    
            if (response == null || response.getData() == null) {
                return 0;
            }
            return response.getData().size(); // total aktivitas = jumlah purchase
    
        } catch (Exception e) {
            System.err.println("Exception saat ambil purchases: " + e.getMessage());
            return 0;
        }
    }

    private List<AssetDTO> fetchAssets(UUID supplierId) {
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }
    
        String url = assetUrl + "/api/asset/by-supplier/" + supplierId;
    
        try {
            BaseResponseDTO<List<AssetDTO>> response = webClient
                .get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> {
                    if (res.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        System.out.println("Tidak ada asset ditemukan untuk supplier ID: " + supplierId);
                        return Mono.empty();
                    }
                    return Mono.error(new RuntimeException("Client error: " + res.statusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, res -> {
                    System.err.println("Server error saat mengambil asset: " + res.statusCode());
                    return Mono.error(new RuntimeException("Server error: " + res.statusCode()));
                })
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<AssetDTO>>>() {})
                .block();
    
            if (response == null || response.getData() == null) {
                return new ArrayList<>();
            }
            return response.getData();
    
        } catch (Exception e) {
            System.err.println("Exception saat ambil asset: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<PurchaseResponseDTO> fetchPurchases(UUID supplierId) {
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }
    
        String url = purchaseUrl + "/api/purchase/supplier/" + supplierId;
    
        try {
            BaseResponseDTO<List<PurchaseResponseDTO>> response = webClient
                .get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> {
                    if (res.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        System.out.println("Tidak ada purchase ditemukan untuk supplier ID: " + supplierId);
                        return Mono.empty();
                    }
                    return Mono.error(new RuntimeException("Client error: " + res.statusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, res -> {
                    System.err.println("Server error saat mengambil purchase: " + res.statusCode());
                    return Mono.error(new RuntimeException("Server error: " + res.statusCode()));
                })
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<PurchaseResponseDTO>>>() {})
                .block();
    
            if (response == null || response.getData() == null) {
                return new ArrayList<>();
            }
            return response.getData();
    
        } catch (Exception e) {
            System.err.println("Exception saat ambil purchase: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<ResourceResponseDTO> fetchResources(UUID supplierId) {
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }
    
        String url = resourceUrl + "/api/resource/find-by-supplier/" + supplierId;
    
        try {
            BaseResponseDTO<List<ResourceResponseDTO>> response = webClient
                .get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> {
                    if (res.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        System.out.println("Tidak ada resource ditemukan untuk supplier ID: " + supplierId);
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

    // Helper method to format currency (Rp) with thousands separator
    private String formatCurrency(int amount) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        return numberFormat.format(amount);
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
        supplier.setPurchaseIds(new ArrayList<>());
    
        Supplier savedSupplier = supplierRepository.save(supplier);
    
        // Tambahan: setelah save, hubungkan supplier ke resource-resource
        if (!resourceIds.isEmpty()) {
            addSupplierIdToResources(savedSupplier.getId(), resourceIds);
        }
    
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
    
        Integer totalPurchases = supplier.getPurchaseIds().size();
        dto.setTotalPurchases(totalPurchases != null ? totalPurchases : 0);
    
        return dto;
    }
    

    @Override
    public String getSupplierName(UUID supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier tidak ditemukan");
        }

        String name = supplier.getNameSupplier();
        return name;
    }

    @Override
    public SupplierResponseDTO updateSupplier(UpdateSupplierRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier tidak ditemukan."));
    
        // Validasi nomor telepon
        if (dto.getNoTelpSupplier() != null && !dto.getNoTelpSupplier().matches("\\d+")) {
            throw new IllegalArgumentException("Nomor telepon hanya boleh terdiri dari angka.");
        }
    
        // Validasi unik
        if (dto.getNoTelpSupplier() != null &&
            supplierRepository.existsByNoTelpSupplierAndIdNot(dto.getNoTelpSupplier(), dto.getId())) {
            throw new IllegalArgumentException("Nomor telepon sudah digunakan.");
        }
        if (dto.getEmailSupplier() != null &&
            supplierRepository.existsByEmailSupplierAndIdNot(dto.getEmailSupplier(), dto.getId())) {
            throw new IllegalArgumentException("Email sudah digunakan.");
        }
    
        if (dto.getNameSupplier() != null && supplierRepository.existsByNameSupplierAndIdNot(dto.getNameSupplier(), dto.getId())) {
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
    
        Supplier savedSupplier = supplierRepository.save(supplier);
    
        // 🔥 Tambahan: setelah update, sinkronisasi resource-resource
        updateSupplierIdInResources(savedSupplier.getId(), resourceIds);
    
        return supplierToSupplierResponseDTO(savedSupplier);
    }

    @Override
    public void addPurchaseId(UUID supplierId, String purchaseId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier tidak ditemukan."));
        
        supplier.getPurchaseIds().add(purchaseId);
        supplierRepository.save(supplier);
    }

    @Override
    public DetailSupplierDTO getSupplierDetail(UUID supplierId) {
        // Fetch Supplier details
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        // Fetch related assets, purchases, and resources
        List<AssetDTO> assets = fetchAssets(supplierId);
        List<PurchaseResponseDTO> purchases = fetchPurchases(supplierId);
        List<ResourceResponseDTO> resources = fetchResources(supplierId);

        // Format activityName for each purchase
        purchases.forEach(purchase -> {
            String formattedPrice = formatCurrency(purchase.getPurchasePrice());
            String activityName = "Pembelian " + purchase.getPurchaseType() +
                    " seharga Rp" + formattedPrice +;
            purchase.setActivityName(activityName); // Set the formatted activityName
        });

        // Prepare the response DTO
        DetailSupplierDTO detailSupplierDTO = new DetailSupplierDTO();
        detailSupplierDTO.setSupplierName(supplier.getNameSupplier());
        detailSupplierDTO.setSupplierPhone(supplier.getNoTelpSupplier());
        detailSupplierDTO.setSupplierEmail(supplier.getEmailSupplier());
        detailSupplierDTO.setSupplierCompany(supplier.getCompanySupplier());
        detailSupplierDTO.setSupplierAddress(supplier.getAddressSupplier());
        detailSupplierDTO.setAssets(assets);
        detailSupplierDTO.setPurchases(purchases);
        detailSupplierDTO.setResources(resources);

        return detailSupplierDTO;
    }
}
