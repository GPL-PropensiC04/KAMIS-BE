package gpl.karina.purchase.restservice;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.purchase.model.AssetTemp;
import gpl.karina.purchase.model.Purchase;
import gpl.karina.purchase.model.ResourceTemp;
import gpl.karina.purchase.repository.AssetTempRepository;
import gpl.karina.purchase.repository.PurchaseRepository;
import gpl.karina.purchase.repository.ResourceTempRepository;
import gpl.karina.purchase.restdto.request.AddPurchaseDTO;
import gpl.karina.purchase.restdto.request.AssetTempDTO;
import gpl.karina.purchase.restdto.request.ResourceTempDTO;
import gpl.karina.purchase.restdto.request.UpdatePurchaseDTO;
import gpl.karina.purchase.restdto.request.UpdatePurchaseStatusDTO;
import gpl.karina.purchase.restdto.response.AssetTempResponseDTO;
import gpl.karina.purchase.restdto.response.BaseResponseDTO;
import gpl.karina.purchase.restdto.response.PurchaseResponseDTO;
import gpl.karina.purchase.restdto.response.ResourceResponseDTO;
import gpl.karina.purchase.restdto.response.ResourceTempResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PurchaseRestServiceImplb implements PurchaseRestService {

    private final PurchaseRepository purchaseRepository;
    private final AssetTempRepository assetTempRepository;
    private final ResourceTempRepository resourceTempRepository;
    private final WebClient webClientResource;
    private final HttpServletRequest request;


    public PurchaseRestServiceImplb(PurchaseRepository purchaseRepository, AssetTempRepository assetTempRepository, 
                                    ResourceTempRepository resourceTempRepository, WebClient.Builder webClientBuilder, HttpServletRequest request) {
        this.purchaseRepository = purchaseRepository;
        this.assetTempRepository = assetTempRepository;
        this.resourceTempRepository = resourceTempRepository;
        this.webClientResource = webClientBuilder.baseUrl("http://localhost:8085/api").build();
        this.request = request;
    }

    private ResourceResponseDTO getResourceFromResourceService(Long resourceId) throws IllegalArgumentException {
        var response = webClientResource
                .get()
                .uri("/resource/" + resourceId)
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceResponseDTO>>() {})
                .block();
        
        return response.getData();
    }

<<<<<<< HEAD
    private ResourceResponseDTO addResourceToResourceDatabase(Long resourceId, Integer resourceStock) throws IllegalArgumentException {
        var response = webClientResource
                .get()
                .uri("/resource/" + resourceId + "/" + resourceStock)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceResponseDTO>>() {})
                .block();
        
        return response.getData();
    }

    public BillResponseDTO addBill(String idAppointment, String token, UUID idPatient) {

        

    
        // Menggunakan POST untuk membuat Bill baru
        var response = webClient.post()
                                 .uri("/api/bill/add/forAppointment")  // URL API
                                 .header("Authorization", "Bearer " + token)
                                 .bodyValue(billRequest)  // Mengirimkan body dengan payload
                                 .retrieve()  // Mengambil response
                                 .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<BillResponseDTO>>() {})
                                 .block();  // Menunggu responsenya
    
        // Memeriksa apakah response null atau statusnya tidak OK
        if (response == null || response.getStatus() != 200) {
            return null;  // Jika response null atau statusnya tidak OK (200), return null
        }
    
        // Mengembalikan data BillResponseDTO jika semuanya baik-baik saja
        return response.getData();
    }

    private ResourceResponseDTO addResourceToResourceDatabase(Long resourceId, Integer resourceStock) throws IllegalArgumentException {
        var response = webClientResource
                .get()
                .uri("/resource/" + resourceId + "/" + resourceStock)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceResponseDTO>>() {})
                .block();
        
        return response.getData();
=======
    public String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
>>>>>>> 40e8269bd221cad19ce71c2b70741f5b0aaa65e9
    }

    private ResourceTempResponseDTO resourceTempToResourceTempResponseDTO (ResourceTemp resourceTemp) {
        ResourceTempResponseDTO resourceTempResponseDTO = new ResourceTempResponseDTO();
        resourceTempResponseDTO.setResourceId(resourceTemp.getResourceId());
        resourceTempResponseDTO.setResourceName(resourceTemp.getResourceName());
        resourceTempResponseDTO.setResourceTotal(resourceTemp.getResourceTotal());
        resourceTempResponseDTO.setResourcePrice(resourceTemp.getResourcePrice());
        return resourceTempResponseDTO;
    }

    private AssetTempResponseDTO assetTempToAssetTempResponseDTO (AssetTemp assetTemp) {
        AssetTempResponseDTO assetTempResponseDTO = new AssetTempResponseDTO();
        assetTempResponseDTO.setAssetNameString(assetTemp.getAssetName());
        assetTempResponseDTO.setAssetDescription(assetTemp.getAssetDescription());
        assetTempResponseDTO.setAssetType(assetTemp.getAssetType());
        assetTempResponseDTO.setAssetPrice(assetTemp.getAssetPrice());
        assetTempResponseDTO.setFotoContentType(assetTemp.getFotoContentType());
        assetTempResponseDTO.setFotoUrl("/api/purchase/asset/" + assetTemp.getId() + "/foto"); // Tambahkan ini
        return assetTempResponseDTO;
    }

    private PurchaseResponseDTO purchaseToPurchaseResponseDTO(Purchase purchase) {
        PurchaseResponseDTO purchaseResponseDTO = new PurchaseResponseDTO();
        purchaseResponseDTO.setPurchaseId(purchase.getId());
        purchaseResponseDTO.setPurchaseSubmissionDate(purchase.getPurchaseSubmissionDate());
        purchaseResponseDTO.setPurchaseUpdateDate(purchase.getPurchaseUpdateDate());
        purchaseResponseDTO.setPurchaseSupplier(purchase.getPurchaseSupplier());
        purchaseResponseDTO.setPurchasePrice(purchase.getPurchasePrice());
        purchaseResponseDTO.setPurchaseNote(purchase.getPurchaseNote());

        Boolean purchaseType = purchase.isPurchaseType();
        if (purchaseType) {
            purchaseResponseDTO.setPurchaseType("Resource");
            
            List<ResourceTemp> resourceTemps = purchase.getPurchaseResource();
            List<ResourceTempResponseDTO> resourceTempResponseDTOs = new ArrayList<>();
            for (ResourceTemp resourceTemp: resourceTemps) {
                resourceTempResponseDTOs.add(resourceTempToResourceTempResponseDTO(resourceTemp));
            }
            purchaseResponseDTO.setPurchaseResource(resourceTempResponseDTOs);
        } else {
            purchaseResponseDTO.setPurchaseType("Aset");

            AssetTemp assetTemp = assetTempRepository.findById(purchase.getPurchaseAsset()).orElse(null);
            purchaseResponseDTO.setPurchaseAsset(assetTempToAssetTempResponseDTO(assetTemp));
        }
        
        purchaseResponseDTO.setPurchaseStatus(purchase.getPurchaseStatus());

        return purchaseResponseDTO;
    }

    @Override
    public PurchaseResponseDTO addPurchase(AddPurchaseDTO addPurchaseDTO) {
        if (addPurchaseDTO.getPurchaseSupplier() == null) {
            throw new IllegalArgumentException("Supplier tidak boleh kosong");
        }

        Purchase purchase = new Purchase();
        purchase.setPurchaseSupplier(addPurchaseDTO.getPurchaseSupplier());
        purchase.setPurchaseType(addPurchaseDTO.isPurchaseType());
        purchase.setPurchaseStatus("Diajukan");
        purchase.setPurchaseNote(addPurchaseDTO.getPurchaseNote());

        String id = "";
        if (purchase.isPurchaseType()) {
            id += "R-";

            List<ResourceTempDTO> resourceDTO = addPurchaseDTO.getPurchaseResource();
            if (resourceDTO == null || resourceDTO.isEmpty()) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian resource, pastikan menginput data resource setidaknya satu.");
            }
            if (addPurchaseDTO.getPurchaseAsset() != null) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian resource, pastikan tidak menginput data aset.");
            }

            List<ResourceTemp> resourceTemps = new ArrayList<>();
            Integer purchasePrice = 0;

            Set<Long> existingIds = new HashSet<>();
            for (ResourceTempDTO resourceInput : resourceDTO) {
                if (resourceInput.getResourceId() != null && !existingIds.add(resourceInput.getResourceId())) {
                    throw new IllegalArgumentException("Tidak boleh terdapat lebih dari satu resource yang sama!");
                }

                ResourceResponseDTO resourceCheck = getResourceFromResourceService(resourceInput.getResourceId());
                if (resourceCheck == null) {
                    throw new IllegalArgumentException("Resource Tidak Terdaftar pada Sistem.");
                }
                if (!resourceCheck.getResourceName().equals(resourceInput.getResourceName())) {
                    throw new IllegalArgumentException("Nama Resource Tidak Sesuai dengan Id pada Sistem.");
                }

                ResourceTemp resourceTemp = new ResourceTemp();
                resourceTemp.setResourceId(resourceInput.getResourceId());
                resourceTemp.setResourceName(resourceInput.getResourceName());
                resourceTemp.setResourceTotal(resourceInput.getResourceTotal());
                resourceTemp.setResourcePrice(resourceInput.getResourcePrice());

                purchasePrice += resourceTemp.getResourcePrice() * resourceTemp.getResourceTotal();
                resourceTempRepository.save(resourceTemp);
                resourceTemps.add(resourceTemp);
            }
            purchase.setPurchaseResource(resourceTemps);
            purchase.setPurchasePrice(purchasePrice);
        } else {
            id += "A-";

            if (addPurchaseDTO.getPurchaseAsset() == null) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian aset, pastikan menginput data aset.");
            }
            if (addPurchaseDTO.getPurchaseResource() != null) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian aset, pastikan tidak menginput data resource.");
            }

            AssetTemp assetTemp = assetTempRepository.findById(addPurchaseDTO.getPurchaseAsset()).orElseThrow(() -> new IllegalArgumentException("Aset tidak ditemukan dalam database."));
            purchase.setPurchaseAsset(assetTemp.getId());
            purchase.setPurchasePrice(assetTemp.getAssetPrice());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        Date today = new Date(); // Get current date

        id += sdf.format(today) + "-";

        // Fetch all purchases and filter for today
        List<Purchase> allPurchases = purchaseRepository.findAll();
        long purchasesCountToday = allPurchases.stream()
            .filter(p -> isSameDay(p.getPurchaseSubmissionDate(), today)) // Renamed `purchase` to `p`
            .count();

        long newIdNumber = purchasesCountToday + 1;

        id += String.format("%03d", newIdNumber);
        purchase.setId(id);

        Purchase newPurchase = purchaseRepository.save(purchase);

        return purchaseToPurchaseResponseDTO(newPurchase);
    }
    
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public List<PurchaseResponseDTO> getAllPurchase(Integer startNominal, Integer endNominal,
                                                    Boolean highNominal, Date startDate, Date endDate, 
                                                    Boolean newDate, String type, String idSearch) {
        List<Purchase> purchases = purchaseRepository.findAll();

        // Adjust endDate to include the whole day (set time to 23:59:59)
        final Date adjustedEndDate;
        if (endDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            adjustedEndDate = calendar.getTime();
        } else {
            adjustedEndDate = null;
        }

        
        List<PurchaseResponseDTO> filteredPurchases = purchases.stream()
            // Filter berdasarkan range harga
            .filter(p -> (startNominal == null || p.getPurchasePrice() >= startNominal) &&
                        (endNominal == null || p.getPurchasePrice() <= endNominal))
            
            // Filter berdasarkan range tanggal
            .filter(p -> (startDate == null || !p.getPurchaseSubmissionDate().before(startDate)) &&
                        (adjustedEndDate == null || !p.getPurchaseSubmissionDate().after(adjustedEndDate)))
            
            // Filter berdasarkan tipe pembelian (0 = Aset, 1 = Resource, "all" untuk semua)
            .filter(p -> "all".equalsIgnoreCase(type) ||
                        ("aset".equalsIgnoreCase(type) && !p.isPurchaseType()) ||
                        ("resource".equalsIgnoreCase(type) && p.isPurchaseType()))
            
            // Filter berdasarkan ID yang mengandung substring tertentu
            .filter(p -> idSearch == null || p.getId().contains(idSearch))
            
            // Konversi ke DTO terlebih dahulu
            .map(this::purchaseToPurchaseResponseDTO)
            
            // Sorting berdasarkan harga atau tanggal
            .sorted((p1, p2) -> {
                boolean sortByPrice = highNominal != null; // Hanya sorting jika highNominal tidak null
                boolean sortByDate = Boolean.TRUE.equals(newDate);
                
                if (sortByPrice) {
                    return highNominal ? Integer.compare(p2.getPurchasePrice(), p1.getPurchasePrice()) // Descending price
                                    : Integer.compare(p1.getPurchasePrice(), p2.getPurchasePrice()); // Ascending price
                } else if (sortByDate) {
                    return p2.getPurchaseSubmissionDate().compareTo(p1.getPurchaseSubmissionDate()); // Descending date
                }
                return p1.getPurchaseSubmissionDate().compareTo(p2.getPurchaseSubmissionDate()); // Default ascending date
            })


                
            .collect(Collectors.toList());

        return filteredPurchases;
    }

    @Override
    public PurchaseResponseDTO updatePurchase(UpdatePurchaseDTO updatePurchaseDTO, String purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Pembelian dengan Id " + purchaseId + " tidak ditemukan."));
        
        String purchaseStatus = purchase.getPurchaseStatus();
        if (!purchaseStatus.equals("Diajukan")) {
            throw new IllegalArgumentException("Detail Pembelian sudah tidak bisa diperbarui.");
        }
        
        purchase.setPurchaseSupplier(updatePurchaseDTO.getPurchaseSupplier());
        purchase.setPurchaseNote(updatePurchaseDTO.getPurchaseNote());

        boolean purchaseType = purchase.isPurchaseType();
        if (purchaseType) {
            List<ResourceTemp> existingResources = new ArrayList<>(purchase.getPurchaseResource());
            List<ResourceTempDTO> resourceDTOs = updatePurchaseDTO.getPurchaseResource();

            if (resourceDTOs == null || resourceDTOs.isEmpty()) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian resource, pastikan menginput data resource setidaknya satu.");
            }

            Set<Long> existingIds = new HashSet<>();
            List<ResourceTemp> updatedResources = new ArrayList<>();
            Integer totalPurchasePrice = 0;

            // Mapping existing resources by ID
            Map<Long, ResourceTemp> existingResourceMap = existingResources.stream()
                .collect(Collectors.toMap(ResourceTemp::getResourceId, Function.identity()));

            // Iterasi DTO untuk update dan penambahan resource
            for (ResourceTempDTO resourceDTO : resourceDTOs) {
                if (resourceDTO.getResourceId() != null && !existingIds.add(resourceDTO.getResourceId())) {
                    throw new IllegalArgumentException("Tidak boleh terdapat lebih dari satu resource yang sama!");
                }

                ResourceResponseDTO resourceCheck = getResourceFromResourceService(resourceDTO.getResourceId());
                if (resourceCheck == null) {
                    throw new IllegalArgumentException("Resource Tidak Terdaftar pada Sistem.");
                }
                if (!resourceCheck.getResourceName().equals(resourceDTO.getResourceName())) {
                    throw new IllegalArgumentException("Nama Resource Tidak Sesuai dengan Id pada Sistem.");
                }

                ResourceTemp resourceTemp;
                
                if (existingResourceMap.containsKey(resourceDTO.getResourceId())) {
                    // Update existing resource
                    resourceTemp = existingResourceMap.get(resourceDTO.getResourceId());
                } else {
                    // Create new resource
                    resourceTemp = new ResourceTemp();
                    resourceTemp.setResourceId(resourceDTO.getResourceId());
                }

                resourceTemp.setResourceName(resourceDTO.getResourceName());
                resourceTemp.setResourceTotal(resourceDTO.getResourceTotal());
                resourceTemp.setResourcePrice(resourceDTO.getResourcePrice());

                totalPurchasePrice += resourceTemp.getResourcePrice() * resourceTemp.getResourceTotal();
                updatedResources.add(resourceTemp);
            }

            // Hapus resource yang tidak ada di DTO
            List<ResourceTemp> resourcesToRemove = existingResources.stream()
                .filter(resource -> !existingIds.contains(resource.getResourceId()))
                .collect(Collectors.toList());

            resourceTempRepository.deleteAll(resourcesToRemove);

            // Simpan perubahan
            resourceTempRepository.saveAll(updatedResources);
            purchase.setPurchaseResource(updatedResources);
            purchase.setPurchasePrice(totalPurchasePrice);
        }

        if (!purchaseType) {
            if (updatePurchaseDTO.getPurchaseResource() != null) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian aset, pastikan tidak menginput data resource.");
            }
        }

        Purchase updatedPurchase = purchaseRepository.save(purchase);

        return purchaseToPurchaseResponseDTO(updatedPurchase);
    }

    @Override
    public AssetTempResponseDTO addAsset(AssetTempDTO assetTempDTO) {
        if (assetTempDTO.getAssetName() == null) {
            throw new IllegalArgumentException("Nama Aset tidak boleh kosong");
        }
        if (assetTempDTO.getAssetDescription() == null) {
            throw new IllegalArgumentException("Deskripsi Aset tidak boleh kosong");
        }
        if (assetTempDTO.getAssetType() == null) {
            throw new IllegalArgumentException("Tipe Aset tidak boleh kosong");
        }
        if (assetTempDTO.getAssetPrice() == null) {
            throw new IllegalArgumentException("Harga Aset tidak boleh kosong");
        }

        AssetTemp assetTemp = new AssetTemp();
        assetTemp.setAssetName(assetTempDTO.getAssetName());
        assetTemp.setAssetDescription(assetTempDTO.getAssetDescription());
        assetTemp.setAssetType(assetTempDTO.getAssetType());
        assetTemp.setAssetPrice(assetTempDTO.getAssetPrice());

        if (assetTempDTO.getFoto() != null && !assetTempDTO.getFoto().isEmpty()) {
            try {
                assetTemp.setFoto(assetTempDTO.getFoto().getBytes());
                assetTemp.setFotoContentType(assetTempDTO.getFoto().getContentType());
            } catch (IOException e) {
                throw new IllegalArgumentException("Gagal mengupload foto");
            }
        }

        AssetTemp newAssetTemp = assetTempRepository.save(assetTemp);
        return assetTempToAssetTempResponseDTO(newAssetTemp);
    }

    @Override
    public List<AssetTempResponseDTO> getAllAssets() {
        List<AssetTemp> assets = assetTempRepository.findAll();
        return assets.stream()
            .map(this::assetTempToAssetTempResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public PurchaseResponseDTO getDetailPurchase(String purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Pembelian dengan Id " + purchaseId + " tidak ditemukan."));   
        return purchaseToPurchaseResponseDTO(purchase);
    }

    @Override
    public PurchaseResponseDTO updatePurchaseStatusToNext(UpdatePurchaseStatusDTO updatePurchaseStatusDTO, String purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Pembelian dengan Id " + purchaseId + " tidak ditemukan."));
        
        String purchaseStatus = purchase.getPurchaseStatus();
        if (purchaseStatus.equals("Selesai") || purchaseStatus.equals("Dibatalkan") || purchaseStatus.equals("Ditolak")) {
            throw new IllegalArgumentException("Status Pembelian sudah tidak bisa diperbarui.");
        }
        
        purchase.setPurchaseNote(updatePurchaseStatusDTO.getPurchaseNote());

        if (purchaseStatus.equals("Diajukan")) {
            // cek role yang mau update, Direksi atau Financial kah? kalo iya, update status ke Disetujui
            // kalo bukan, throw exception
            purchase.setPurchaseStatus("Disetujui");
        } 
        else if (purchaseStatus.equals("Disetujui")) {
            // cek role yang mau update, Operational kah? kalo iya, update status ke Diproses
            purchase.setPurchaseStatus("Diproses");
        } 
        else if (purchaseStatus.equals("Diproses")) {
            // cek role yang mau update, Operational kah? kalo iya, update status ke Selesai
            if (purchase.isPurchaseType()) {
                List<ResourceTemp> resources = purchase.getPurchaseResource();
                for (ResourceTemp resource : resources) {
                    ResourceResponseDTO resourceUpdate = addResourceToResourceDatabase(resource.getResourceId(), resource.getResourceTotal());
                }
            
            } else {
                Map<String, Object> assetRequest = new HashMap<>();
                assetRequest.put("appointmentId", idAppointment);
                assetRequest.put("patientId", idPatient);
                AssetTemp assetTemp = assetTempRepository.findById(purchase.getPurchaseAsset()).orElseThrow(() -> new IllegalArgumentException("Aset tidak ditemukan dalam database."));
                purchase.setPurchaseStatus("Selesai");
            }
            purchase.setPurchaseStatus("Selesai");
        }
        
        boolean purchaseType = purchase.isPurchaseType();
        if (purchaseType) {
            List<ResourceTemp> existingResources = new ArrayList<>(purchase.getPurchaseResource());
            List<ResourceTempDTO> resourceDTOs = updatePurchaseDTO.getPurchaseResource();

            if (resourceDTOs == null || resourceDTOs.isEmpty()) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian resource, pastikan menginput data resource setidaknya satu.");
            }

            Set<Long> existingIds = new HashSet<>();
            List<ResourceTemp> updatedResources = new ArrayList<>();
            Integer totalPurchasePrice = 0;

            // Mapping existing resources by ID
            Map<Long, ResourceTemp> existingResourceMap = existingResources.stream()
                .collect(Collectors.toMap(ResourceTemp::getResourceId, Function.identity()));

            // Iterasi DTO untuk update dan penambahan resource
            for (ResourceTempDTO resourceDTO : resourceDTOs) {
                if (resourceDTO.getResourceId() != null && !existingIds.add(resourceDTO.getResourceId())) {
                    throw new IllegalArgumentException("Tidak boleh terdapat lebih dari satu resource yang sama!");
                }

                ResourceResponseDTO resourceCheck = getResourceFromResourceService(resourceDTO.getResourceId());
                if (resourceCheck == null) {
                    throw new IllegalArgumentException("Resource Tidak Terdaftar pada Sistem.");
                }
                if (!resourceCheck.getResourceName().equals(resourceDTO.getResourceName())) {
                    throw new IllegalArgumentException("Nama Resource Tidak Sesuai dengan Id pada Sistem.");
                }

                ResourceTemp resourceTemp;
                
                if (existingResourceMap.containsKey(resourceDTO.getResourceId())) {
                    // Update existing resource
                    resourceTemp = existingResourceMap.get(resourceDTO.getResourceId());
                } else {
                    // Create new resource
                    resourceTemp = new ResourceTemp();
                    resourceTemp.setResourceId(resourceDTO.getResourceId());
                }

                resourceTemp.setResourceName(resourceDTO.getResourceName());
                resourceTemp.setResourceTotal(resourceDTO.getResourceTotal());
                resourceTemp.setResourcePrice(resourceDTO.getResourcePrice());

                totalPurchasePrice += resourceTemp.getResourcePrice() * resourceTemp.getResourceTotal();
                updatedResources.add(resourceTemp);
            }

            // Hapus resource yang tidak ada di DTO
            List<ResourceTemp> resourcesToRemove = existingResources.stream()
                .filter(resource -> !existingIds.contains(resource.getResourceId()))
                .collect(Collectors.toList());

            resourceTempRepository.deleteAll(resourcesToRemove);

            // Simpan perubahan
            resourceTempRepository.saveAll(updatedResources);
            purchase.setPurchaseResource(updatedResources);
            purchase.setPurchasePrice(totalPurchasePrice);
        }

        if (!purchaseType) {
            if (updatePurchaseDTO.getPurchaseResource() != null) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian aset, pastikan tidak menginput data resource.");
            }
        }

        Purchase updatedPurchase = purchaseRepository.save(purchase);

        return purchaseToPurchaseResponseDTO(updatedPurchase);
    }

    public AssetTempResponseDTO getDetailAsset(Long id) {
        AssetTemp asset = assetTempRepository.findById(id).orElse(null);
        return assetTempToAssetTempResponseDTO(asset);
    }

}
