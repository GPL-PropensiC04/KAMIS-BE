package gpl.karina.purchase.restservice;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.beans.factory.annotation.Value;
import gpl.karina.purchase.model.AssetTemp;
import gpl.karina.purchase.model.LogPurchase;
import gpl.karina.purchase.model.Purchase;
import gpl.karina.purchase.model.ResourceTemp;
import gpl.karina.purchase.repository.AssetTempRepository;
import gpl.karina.purchase.repository.LogPurchaseRepository;
import gpl.karina.purchase.repository.PurchaseRepository;
import gpl.karina.purchase.repository.ResourceTempRepository;
import gpl.karina.purchase.restdto.request.AddPurchaseDTO;
import gpl.karina.purchase.restdto.request.AddPurchaseIdDTO;
import gpl.karina.purchase.restdto.request.AssetTempDTO;
import gpl.karina.purchase.restdto.request.ResourceTempDTO;
import gpl.karina.purchase.restdto.request.UpdatePurchaseDTO;
import gpl.karina.purchase.restdto.request.UpdatePurchaseStatusDTO;
import gpl.karina.purchase.restdto.response.AssetTempResponseDTO;
import gpl.karina.purchase.restdto.response.BaseResponseDTO;
import gpl.karina.purchase.restdto.response.LogPurchaseResponseDTO;
import gpl.karina.purchase.restdto.response.PurchaseListResponseDTO;
import gpl.karina.purchase.restdto.response.PurchaseResponseDTO;
import gpl.karina.purchase.restdto.response.ResourceResponseDTO;
import gpl.karina.purchase.restdto.response.ResourceTempResponseDTO;
import gpl.karina.purchase.security.jwt.JwtUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class PurchaseRestServiceImpl implements PurchaseRestService {

    @Value("${purchase.app.resourceUrl}")
    private String resourceUrl;
    @Value("${purchase.app.assetUrl}")
    private String assetUrl;
    @Value("${purchase.app.profileUrl}")
    private String profileUrl;
    private final PurchaseRepository purchaseRepository;
    private final AssetTempRepository assetTempRepository;
    private final ResourceTempRepository resourceTempRepository;
    private final LogPurchaseRepository logPurchaseRepository;
    private final WebClient.Builder webClientBuilder;
    private final HttpServletRequest request;
    private final JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(PurchaseRestServiceImpl.class);

    private WebClient webClientResource;
    private WebClient webClientAsset;
    private WebClient webClientProfile;

    public PurchaseRestServiceImpl(PurchaseRepository purchaseRepository, AssetTempRepository assetTempRepository,
            ResourceTempRepository resourceTempRepository, WebClient.Builder webClientBuilder,
            HttpServletRequest request, JwtUtils jwtUtils, LogPurchaseRepository logPurchaseRepository) {
        this.purchaseRepository = purchaseRepository;
        this.assetTempRepository = assetTempRepository;
        this.resourceTempRepository = resourceTempRepository;
        this.webClientBuilder = webClientBuilder;
        this.request = request;
        this.jwtUtils = jwtUtils;
        this.logPurchaseRepository = logPurchaseRepository;
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing WebClients with URLs:");
        logger.info("Resource URL: {}", resourceUrl);
        logger.info("Asset URL: {}", assetUrl);
        this.webClientResource = webClientBuilder
                .baseUrl(resourceUrl)
                .build();

        this.webClientAsset = webClientBuilder
                .baseUrl(assetUrl)
                .build();
        
        this.webClientProfile = webClientBuilder
                .baseUrl(profileUrl)
                .build();
    }

    private String getSupplierName(String id) {
        try {
            var response = webClientProfile
                    .get()
                    .uri("/supplier/name/" + id)
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<String>>() {
                    })
                    .block();
            return response.getData();
        } catch (Exception e) {
            logger.error("Error calling resource service: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void addPurchaseIdToSupplier(AddPurchaseIdDTO addPurchaseIdDTO) {
        try {
            webClientProfile
                    .put()
                    .uri("/supplier/add-purchase")
                    .bodyValue(addPurchaseIdDTO)
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<Void>>() {
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Error calling resource service: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private ResourceResponseDTO getResourceFromResourceService(Long resourceId) {
        try {
            var response = webClientResource
                    .get()
                    .uri("/resource/find/" + resourceId)
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceResponseDTO>>() {
                    })
                    .block();
            return response.getData();
        } catch (Exception e) {
            logger.error("Error calling resource service: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private ResourceResponseDTO addResourceToResourceDatabase(Long resourceId, Integer resourceStock)
            throws IllegalArgumentException {
        var response = webClientResource
                .put()
                .uri("/resource/addToDb/" + resourceId + "/" + resourceStock)
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceResponseDTO>>() {
                })
                .block();

        return response.getData();
    }

    public AssetTempResponseDTO addAssetToAssetDatabase(Map<String, Object> assetRequest) {


        try {
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("platNomor", assetRequest.get("platNomor"));
            formData.add("assetName", assetRequest.get("assetName"));
            formData.add("assetDescription", assetRequest.get("assetDescription"));
            formData.add("assetType", assetRequest.get("assetType"));
            formData.add("assetPrice", assetRequest.get("assetPrice"));
            formData.add("tanggalPerolehan", assetRequest.get("tanggalPerolehan"));

            // Untuk foto (byte[]), bungkus ke ByteArrayResource agar dianggap file di
            // form-data
            byte[] fotoBytes = (byte[]) assetRequest.get("foto");
            if (fotoBytes != null) {
                ByteArrayResource fotoResource = new ByteArrayResource(fotoBytes) {
                    @Override
                    public String getFilename() {
                        return "foto.jpg"; // Optional, biar backend baca sebagai file
                    }
                };
                formData.add("foto", fotoResource);
            }

            // Kalau fotoContentType mau dipisah / dikirim, bisa juga
            if (assetRequest.get("fotoContentType") != null) {
                formData.add("fotoContentType", assetRequest.get("fotoContentType"));
            }

            var response = webClientAsset.post()
                    .uri("/asset/addAsset")
                    .headers(headers -> {
                        headers.setBearerAuth(getTokenFromRequest());
                    })
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(formData))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<AssetTempResponseDTO>>() {
                    })
                    .doOnError(error -> {
                        logger.error("Error during asset service call: {}", error.getMessage());
                        error.printStackTrace();
                    })
                    .block();

            if (response == null) {
                logger.error("Received null response from asset service");
                return null;
            }

            logger.info("Successfully received response from asset service");
            return response.getData();
        } catch (Exception e) {
            logger.error("Exception in addAssetToAssetDatabase: {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private ResourceTempResponseDTO resourceTempToResourceTempResponseDTO(ResourceTemp resourceTemp) {
        ResourceTempResponseDTO resourceTempResponseDTO = new ResourceTempResponseDTO();
        resourceTempResponseDTO.setResourceId(resourceTemp.getResourceId());
        resourceTempResponseDTO.setResourceName(resourceTemp.getResourceName());
        resourceTempResponseDTO.setResourceTotal(resourceTemp.getResourceTotal());
        resourceTempResponseDTO.setResourcePrice(resourceTemp.getResourcePrice());
        return resourceTempResponseDTO;
    }

    private AssetTempResponseDTO assetTempToAssetTempResponseDTO(AssetTemp assetTemp) {
        AssetTempResponseDTO assetTempResponseDTO = new AssetTempResponseDTO();
        assetTempResponseDTO.setId(assetTemp.getId());
        assetTempResponseDTO.setAssetNameString(assetTemp.getAssetName());
        assetTempResponseDTO.setAssetDescription(assetTemp.getAssetDescription());
        assetTempResponseDTO.setAssetType(assetTemp.getAssetType());
        assetTempResponseDTO.setAssetPrice(assetTemp.getAssetPrice());
        assetTempResponseDTO.setFotoContentType(assetTemp.getFotoContentType());
        assetTempResponseDTO.setFotoUrl("/purchase/asset/" + assetTemp.getId() + "/foto"); // Tambahkan ini
        return assetTempResponseDTO;
    }

    private LogPurchaseResponseDTO logPurchaseToLogPurchaseResponseDTO(LogPurchase logPurchase) {
        LogPurchaseResponseDTO logPurchaseResponseDTO = new LogPurchaseResponseDTO();
        logPurchaseResponseDTO.setId(logPurchase.getId());
        logPurchaseResponseDTO.setUser(logPurchase.getUsername());
        logPurchaseResponseDTO.setAction(logPurchase.getAction());
        logPurchaseResponseDTO.setActionDate(logPurchase.getActionDate());
        return logPurchaseResponseDTO;
    }

    private PurchaseResponseDTO purchaseToPurchaseResponseDTO(Purchase purchase) {
        PurchaseResponseDTO purchaseResponseDTO = new PurchaseResponseDTO();
        purchaseResponseDTO.setPurchaseId(purchase.getId());
        purchaseResponseDTO.setPurchaseSubmissionDate(purchase.getPurchaseSubmissionDate());
        purchaseResponseDTO.setPurchaseUpdateDate(purchase.getPurchaseUpdateDate());
        purchaseResponseDTO.setPurchaseSupplier(purchase.getPurchaseSupplier());
        purchaseResponseDTO.setPurchasePrice(purchase.getPurchasePrice());
        purchaseResponseDTO.setPurchaseNote(purchase.getPurchaseNote());
        purchaseResponseDTO.setPurchasePaymentDate(purchase.getPurchasePaymentDate());
        purchaseResponseDTO.setPurchaseStatus(purchase.getPurchaseStatus());

        Boolean purchaseType = purchase.isPurchaseType();
        if (purchaseType) {
            purchaseResponseDTO.setPurchaseType("Resource");

            List<ResourceTemp> resourceTemps = purchase.getPurchaseResource();
            List<ResourceTempResponseDTO> resourceTempResponseDTOs = new ArrayList<>();
            for (ResourceTemp resourceTemp : resourceTemps) {
                resourceTempResponseDTOs.add(resourceTempToResourceTempResponseDTO(resourceTemp));
            }
            purchaseResponseDTO.setPurchaseResource(resourceTempResponseDTOs);
        } else {
            purchaseResponseDTO.setPurchaseType("Aset");

            AssetTemp assetTemp = assetTempRepository.findById(purchase.getPurchaseAsset()).orElse(null);
            purchaseResponseDTO.setPurchaseAsset(assetTempToAssetTempResponseDTO(assetTemp));
        }

        purchaseResponseDTO.setPurchaseStatus(purchase.getPurchaseStatus());
        
        List<LogPurchase> logs = purchase.getPurchaseLogs();
        List<LogPurchaseResponseDTO> logsDTO = new ArrayList<>();
        for (LogPurchase log : logs) {
            logsDTO.add(logPurchaseToLogPurchaseResponseDTO(log));
        }
        purchaseResponseDTO.setPurchaseLogs(logsDTO);

        return purchaseResponseDTO;
    }

    private PurchaseListResponseDTO purchaseToPurchaseListResponseDTO(Purchase purchase) {
        PurchaseListResponseDTO purchaseResponseDTO = new PurchaseListResponseDTO();
        purchaseResponseDTO.setPurchaseId(purchase.getId());
        purchaseResponseDTO.setPurchaseSubmissionDate(purchase.getPurchaseSubmissionDate());
        purchaseResponseDTO.setPurchaseUpdateDate(purchase.getPurchaseUpdateDate());
        purchaseResponseDTO.setPurchasePrice(purchase.getPurchasePrice());

        String id = String.valueOf(purchase.getPurchaseSupplier());
        purchaseResponseDTO.setPurchaseSupplier(getSupplierName(id));

        purchaseResponseDTO.setPurchaseStatus(purchase.getPurchaseStatus());

        Boolean purchaseType = purchase.isPurchaseType();
        if (purchaseType) {
            purchaseResponseDTO.setPurchaseType("Resource");
        } else {
            purchaseResponseDTO.setPurchaseType("Aset");
        }

        return purchaseResponseDTO;
    }

    private LogPurchase addLog(String action) {
        LogPurchase log = new LogPurchase();

        String username = jwtUtils.getUserNameFromJwtToken(getTokenFromRequest());

        log.setUsername(username);
        log.setAction(action);
        
        Date now = new Date();
        log.setActionDate(now);

        LogPurchase newLog = logPurchaseRepository.save(log);

        return newLog;
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
                throw new IllegalArgumentException(
                        "Anda memilih tipe pembelian resource, pastikan menginput data resource setidaknya satu.");
            }
            if (addPurchaseDTO.getPurchaseAsset() != null) {
                throw new IllegalArgumentException(
                        "Anda memilih tipe pembelian resource, pastikan tidak menginput data aset.");
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
                throw new IllegalArgumentException(
                        "Anda memilih tipe pembelian aset, pastikan tidak menginput data resource.");
            }

            AssetTemp assetTemp = assetTempRepository.findById(addPurchaseDTO.getPurchaseAsset())
                    .orElseThrow(() -> new IllegalArgumentException("Aset tidak ditemukan dalam database."));
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

        purchase.setPurchaseLogs(new ArrayList<>());

        LogPurchase newLog = addLog("Menambahkan Pembelian " + purchase.getId());
        purchase.getPurchaseLogs().add(newLog);

        Purchase newPurchase = purchaseRepository.save(purchase);

        AddPurchaseIdDTO addPurchaseIdDTO = new AddPurchaseIdDTO();
        addPurchaseIdDTO.setPurchaseId(id);
        addPurchaseIdDTO.setSupplierId(purchase.getPurchaseSupplier());
        addPurchaseIdToSupplier(addPurchaseIdDTO);

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
    public List<PurchaseListResponseDTO> getAllPurchase(Integer startNominal, Integer endNominal,
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

        List<PurchaseListResponseDTO> filteredPurchases = purchases.stream()
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
                .map(this::purchaseToPurchaseListResponseDTO)

                // Sorting berdasarkan harga atau tanggal
                .sorted((p1, p2) -> {
                    boolean sortByPrice = highNominal != null; // Hanya sorting jika highNominal tidak null
                    boolean sortByDate = Boolean.TRUE.equals(newDate);

                    if (sortByPrice) {
                        return highNominal ? Integer.compare(p2.getPurchasePrice(), p1.getPurchasePrice()) // Descending
                                                                                                           // price
                                : Integer.compare(p1.getPurchasePrice(), p2.getPurchasePrice()); // Ascending price
                    } else if (sortByDate) {
                        return p2.getPurchaseSubmissionDate().compareTo(p1.getPurchaseSubmissionDate()); // Descending
                                                                                                         // date
                    }
                    return p1.getPurchaseSubmissionDate().compareTo(p2.getPurchaseSubmissionDate()); // Default
                                                                                                     // ascending date
                })

                .collect(Collectors.toList());

        return filteredPurchases;
    }

    @Override
    public PurchaseResponseDTO updatePurchase(UpdatePurchaseDTO updatePurchaseDTO, String purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(
                () -> new IllegalArgumentException("Pembelian dengan Id " + purchaseId + " tidak ditemukan."));

        String purchaseStatus = purchase.getPurchaseStatus();
        if (!purchaseStatus.equals("Diajukan")) {
            throw new IllegalArgumentException("Detail Pembelian sudah tidak bisa diperbarui.");
        }

        StringBuilder logBuilder = new StringBuilder("Memperbarui Pembelian:\n");

        boolean hasChange = false;

        // Cek perubahan supplier
        if (!Objects.equals(purchase.getPurchaseSupplier(), updatePurchaseDTO.getPurchaseSupplier())) {
            String id = String.valueOf(updatePurchaseDTO.getPurchaseSupplier());
            logBuilder.append("  - Mengubah supplier menjadi ").append(getSupplierName(id)).append("\n");
            hasChange = true;
        }

        // Cek perubahan note
        if (!Objects.equals(purchase.getPurchaseNote(), updatePurchaseDTO.getPurchaseNote())) {
            logBuilder.append("  - Mengubah catatan menjadi '").append(updatePurchaseDTO.getPurchaseNote()).append("'\n");
            hasChange = true;
        }

        // Jika tipe resource, cek perubahan resource
        boolean purchaseType = purchase.isPurchaseType();
        if (purchaseType) {
            logBuilder.append("  - Total jenis resource yang dibeli setelah perubahan: ")
                    .append(updatePurchaseDTO.getPurchaseResource().size())
                    .append(" item\n");
            hasChange = true;
        }

        if (!hasChange) {
            logBuilder.append("  - Tidak ada perubahan signifikan\n");
        }

        LogPurchase newLog = addLog(logBuilder.toString());
        purchase.getPurchaseLogs().add(newLog);

        purchase.setPurchaseSupplier(updatePurchaseDTO.getPurchaseSupplier());
        purchase.setPurchaseNote(updatePurchaseDTO.getPurchaseNote());
        if (purchaseType) {
            List<ResourceTemp> existingResources = new ArrayList<>(purchase.getPurchaseResource());
            List<ResourceTempDTO> resourceDTOs = updatePurchaseDTO.getPurchaseResource();

            if (resourceDTOs == null || resourceDTOs.isEmpty()) {
                throw new IllegalArgumentException(
                        "Anda memilih tipe pembelian resource, pastikan menginput data resource setidaknya satu.");
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
                throw new IllegalArgumentException(
                        "Anda memilih tipe pembelian aset, pastikan tidak menginput data resource.");
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
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(
                () -> new IllegalArgumentException("Pembelian dengan Id " + purchaseId + " tidak ditemukan."));
        return purchaseToPurchaseResponseDTO(purchase);
    }

    @Override
    public PurchaseResponseDTO updatePurchaseStatusToNext(UpdatePurchaseStatusDTO updatePurchaseStatusDTO,
            String purchaseId) {
        if (updatePurchaseStatusDTO.getPurchaseNote() == null) {
            throw new IllegalArgumentException("Catatan tidak boleh kosong");
        }
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Pembelian dengan Id " + purchaseId + " tidak ditemukan."));

        String purchaseStatus = purchase.getPurchaseStatus();
        if (purchaseStatus.equals("Selesai") || purchaseStatus.equals("Dibatalkan")
                || purchaseStatus.equals("Ditolak")) {
            throw new IllegalArgumentException("Status Pembelian sudah tidak bisa diperbarui.");
        }

        // Ambil token dari request dan extract role
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }
        String role = jwtUtils.getRoleFromToken(token);

        if (purchaseStatus.equals("Diajukan")) {
            // Role Direksi atau Finance yang boleh update ke Disetujui
            if ((role.equals("Operasional") || role.equals("Admin"))) {
                throw new IllegalArgumentException("Hanya Direksi atau Finance yang dapat menyetujui pembelian.");
            }
            purchase.setPurchaseStatus("Disetujui");
        } else if (purchaseStatus.equals("Disetujui")) {
            // Hanya Operasional yang boleh update ke Diproses
            if (role.equals("Finance") || role.equals("Direksi")) {
                throw new IllegalArgumentException("Hanya Operasional yang dapat memproses pembelian.");
            }
            purchase.setPurchaseStatus("Diproses");
        } else if (purchaseStatus.equals("Diproses")) {
            // Hanya Operasional yang boleh update ke Selesai
            if (role.equals("Finance") || role.equals("Direksi")) {
                throw new IllegalArgumentException("Hanya Operasional atau Admin yang dapat menyelesaikan pembelian.");
            }

            if (purchase.isPurchaseType()) {
                List<ResourceTemp> resources = purchase.getPurchaseResource();
                for (ResourceTemp resource : resources) {
                    ResourceResponseDTO resourceUpdate = addResourceToResourceDatabase(resource.getResourceId(),
                            resource.getResourceTotal());
                }
            } else {
                AssetTemp assetTemp = assetTempRepository.findById(purchase.getPurchaseAsset())
                        .orElseThrow(() -> new IllegalArgumentException("Aset tidak ditemukan dalam database."));
                Map<String, Object> assetRequest = new HashMap<>();
                if (updatePurchaseStatusDTO.getPlatNomor() == null) {
                    throw new IllegalArgumentException("Plat Nomor tidak boleh kosong");
                }
                assetRequest.put("platNomor", updatePurchaseStatusDTO.getPlatNomor());
                assetRequest.put("assetName", assetTemp.getAssetName());
                assetRequest.put("assetDescription", assetTemp.getAssetDescription());
                assetRequest.put("assetType", assetTemp.getAssetType());
                assetRequest.put("assetPrice", assetTemp.getAssetPrice());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                assetRequest.put("tanggalPerolehan", sdf.format(new Date()));
                assetRequest.put("foto", assetTemp.getFoto());
                assetRequest.put("fotoContentType", assetTemp.getFotoContentType());
                AssetTempResponseDTO assetUpdate = addAssetToAssetDatabase(assetRequest);
            }

            // handle supplier di Sprint 2
            purchase.setPurchaseStatus("Selesai");
        }
        purchase.setPurchaseNote(updatePurchaseStatusDTO.getPurchaseNote());
        purchase.setPurchaseUpdateDate(new Date());

        LogPurchase newLog = addLog("Mengubah Status Pembelian menjadi " + purchase.getPurchaseStatus());
        purchase.getPurchaseLogs().add(newLog);

        Purchase updatedPurchase = purchaseRepository.save(purchase);
        return purchaseToPurchaseResponseDTO(updatedPurchase);
    }

    @Override
    public PurchaseResponseDTO updatePurchaseStatusToCancelled(UpdatePurchaseStatusDTO updatePurchaseStatusDTO,
            String purchaseId) {
        if (updatePurchaseStatusDTO.getPurchaseNote() == null) {
            throw new IllegalArgumentException("Catatan tidak boleh kosong");
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Pembelian dengan Id " + purchaseId + " tidak ditemukan."));

        String purchaseStatus = purchase.getPurchaseStatus();
        if (purchaseStatus.equals("Selesai") || purchaseStatus.equals("Dibatalkan")
                || purchaseStatus.equals("Ditolak")) {
            throw new IllegalArgumentException("Status Pembelian sudah tidak bisa diperbarui.");
        }

        // Ambil token dan cek role
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }
        String role = jwtUtils.getRoleFromToken(token);

        if (purchaseStatus.equals("Diajukan")) {
            // Hanya Direksi atau Finance yang boleh update ke Ditolak
            if (!(role.equalsIgnoreCase("Direksi") || role.equalsIgnoreCase("Finance"))) {
                throw new IllegalArgumentException("Hanya Direksi atau Finance yang dapat menolak pembelian.");
            }
            purchase.setPurchaseStatus("Ditolak");
        } else if (purchaseStatus.equals("Disetujui")) {
            // Hanya Operasional yang boleh update ke Dibatalkan
            if (!(role.equalsIgnoreCase("Operasional") || role.equalsIgnoreCase("Admin"))) {
                throw new IllegalArgumentException(
                        "Hanya Operasional atau Admin yang dapat membatalkan pembelian di tahap Disetujui.");
            }
            purchase.setPurchaseStatus("Dibatalkan");
        } else if (purchaseStatus.equals("Diproses")) {
            // Hanya Operasional yang boleh update ke Dibatalkan
            if (!(role.equalsIgnoreCase("Operasional"))) {
                throw new IllegalArgumentException(
                        "Hanya Operasional yang dapat membatalkan pembelian di tahap Diproses.");
            }

            if (purchase.getPurchasePaymentDate() != null) {
                // Next sprint, handle supaya terdeteksi di laporan (refund)
            }
            purchase.setPurchaseStatus("Dibatalkan");
        }
        purchase.setPurchaseNote(updatePurchaseStatusDTO.getPurchaseNote());
        purchase.setPurchaseUpdateDate(new Date());

        LogPurchase newLog = addLog("Mengubah Status Pembelian menjadi " + purchase.getPurchaseStatus());
        purchase.getPurchaseLogs().add(newLog);

        Purchase updatedPurchase = purchaseRepository.save(purchase);
        return purchaseToPurchaseResponseDTO(updatedPurchase);
    }

    @Override
    public PurchaseResponseDTO updatePurchaseStatusPembayaran(UpdatePurchaseStatusDTO updatePurchaseStatusDTO,
            String purchaseId) {
        // Ambil token dan cek role
        if (updatePurchaseStatusDTO.getPurchaseNote() == null) {
            throw new IllegalArgumentException("Catatan tidak boleh kosong");
        }
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalArgumentException("Token tidak ditemukan di header Authorization.");
        }
        String role = jwtUtils.getRoleFromToken(token);

        // Hanya Finance yang boleh update pembayaran
        if (!role.equalsIgnoreCase("Finance")) {
            throw new IllegalArgumentException("Hanya Finance yang dapat mengupdate status pembayaran.");
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Pembelian dengan Id " + purchaseId + " tidak ditemukan."));

        logger.info("Purchase Payment Date: {}", purchase.getPurchasePaymentDate());
        if (purchase.getPurchasePaymentDate() != null) {
            throw new IllegalArgumentException(
                    "Pembayaran sudah dilakukan pada tanggal " + purchase.getPurchasePaymentDate() + ".");
        }

        String purchaseStatus = purchase.getPurchaseStatus();
        // Revisi logika: pembayaran hanya bisa dilakukan jika status "Diproses" atau
        // "Selesai" dan tidak "Ditolak"
        if (purchaseStatus.equals("Dibatalkan")) {
            throw new IllegalArgumentException(
                    "Status Pembayaran tidak bisa diperbarui karena pembelian telah dibatalkan.");
        }
        if (purchaseStatus.equals("Diajukan")) {
            throw new IllegalArgumentException(
                    "Status Pembayaran tidak bisa diperbarui karena pembelian belum disetujui.");
        }
        if (purchaseStatus.equals("Disetujui")) {
            throw new IllegalArgumentException(
                    "Status Pembayaran tidak bisa diperbarui karena pembelian belum diproses.");
        }
        if (purchaseStatus.equals("Ditolak")) {
            throw new IllegalArgumentException(
                    "Status Pembayaran tidak bisa diperbarui karena pembelian telah ditolak.");
        }

        purchase.setPurchaseNote(updatePurchaseStatusDTO.getPurchaseNote());
        purchase.setPurchasePaymentDate(new Date());
        purchase.setPurchaseUpdateDate(new Date());

        LogPurchase newLog = addLog("Mengkonfirmasi status pembayaran telah selesai");
        purchase.getPurchaseLogs().add(newLog);

        Purchase updatedPurchase = purchaseRepository.save(purchase);
        return purchaseToPurchaseResponseDTO(updatedPurchase);
    }

    public AssetTempResponseDTO getDetailAsset(Long id) {
        AssetTemp asset = assetTempRepository.findById(id).orElse(null);
        return assetTempToAssetTempResponseDTO(asset);
    }

}
