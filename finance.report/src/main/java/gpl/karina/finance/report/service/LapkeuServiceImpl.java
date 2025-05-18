package gpl.karina.finance.report.service;

import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.finance.report.dto.response.AssetTempResponseDTO;
import gpl.karina.finance.report.dto.response.BaseResponseDTO;
import gpl.karina.finance.report.dto.response.LapkeuResponseDTO;
import gpl.karina.finance.report.dto.response.ProjectResponseDTO;
import gpl.karina.finance.report.dto.response.PurchaseResponseDTO;
import gpl.karina.finance.report.dto.response.ResourceTempResponseDTO;
import gpl.karina.finance.report.dto.response.MaintenanceResponseDTO;
import gpl.karina.finance.report.model.Lapkeu;
import gpl.karina.finance.report.repository.LapkeuRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class LapkeuServiceImpl implements LapkeuService {

    private final LapkeuRepository lapkeuRepository;

    @Autowired
    private HttpServletRequest request;

    @Value("${finance.report.app.profileUrl}")
    private String profileUrl;

    @Value("${finance.report.app.projectUrl}")
    private String projectUrl;

    @Value("${finance.report.app.purchaseUrl}")
    private String purchaseUrl;

    @Value("${finance.report.app.assetUrl}")
    private String assetUrl;

    private final WebClient webClient = WebClient.create();

    public LapkeuServiceImpl(LapkeuRepository lapkeuRepository) {
        this.lapkeuRepository = lapkeuRepository;
    }

    public String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Date getDateOnly(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    @Override
    public List<LapkeuResponseDTO> fetchAllLapkeu(Date startDate, Date endDate) {
        syncLapkeuFromAllModules();
        List<Lapkeu> lapkeuList = lapkeuRepository.findAll();
        List<Lapkeu> filtered = lapkeuList.stream()
            .filter(l -> {
                if (startDate != null && l.getPaymentDate() != null && l.getPaymentDate().before(startDate)) {
                    return false;
                }
                if (endDate != null && l.getPaymentDate() != null && l.getPaymentDate().after(endDate)) {
                    return false;
                }
                return true;
            })
            .collect(Collectors.toList());
            
        return filtered.stream()
            .map(l -> new LapkeuResponseDTO(
                    l.getId(), l.getActivityType(), l.getPemasukan(), l.getPengeluaran(), l.getDescription(), (Date) l.getPaymentDate()))
            .collect(Collectors.toList());
    }

    public void syncLapkeuFromAllModules() {
        String token = getTokenFromRequest();
        List<Lapkeu> lapkeuList = new ArrayList<>();

        lapkeuList.addAll(fetchProjectLapkeu(token));
        lapkeuList.addAll(fetchPurchaseLapkeu(token));
        lapkeuList.addAll(fetchMaintenanceLapkeu(token));

        for (Lapkeu lapkeu : lapkeuList) {
            lapkeuRepository.save(lapkeu);
        }
    }

    private List<Lapkeu> fetchProjectLapkeu(String token) {
        List<Lapkeu> lapkeuList = new ArrayList<>();
        var projectResponse = webClient.get()
            .uri(projectUrl + "api/project/all")
            .headers(headers -> headers.setBearerAuth(token))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<ProjectResponseDTO>>>() {})
            .block();
        
        if (projectResponse != null) {
            for (var project : projectResponse.getData()) {
                if (project.getProjectPaymentStatus() == 1) {
                    Lapkeu lapkeu = new Lapkeu();
                    lapkeu.setId(project.getId());
                    if (Boolean.TRUE.equals(project.getProjectType())) {
                        lapkeu.setActivityType(1); // 1 = Distribusi
                        lapkeu.setDescription("Distribusi - " + project.getProjectName());
                    } else {
                        lapkeu.setActivityType(0); // 0 = Penjualan
                        lapkeu.setDescription("Penjualan - " + project.getProjectName());
                    }
                    lapkeu.setPemasukan(project.getProjectTotalPemasukkan());
                    lapkeu.setPengeluaran(project.getProjectTotalPengeluaran());
                    lapkeu.setPaymentDate(getDateOnly(project.getProjectPaymentDate()));
                    lapkeuList.add(lapkeu);
                }
            }
        }
        return lapkeuList;
    }

    private AssetTempResponseDTO fetchAssetTempById(String purchaseId, String token) {
        try {
            // 1. Fetch detail purchase
            var purchaseDetailResponse = webClient.get()
                .uri(purchaseUrl + "api/purchase/detail/" + purchaseId)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<PurchaseResponseDTO>>() {})
                .block();

            PurchaseResponseDTO purchase = purchaseDetailResponse != null ? purchaseDetailResponse.getData() : null;
            if (purchase == null) return null;

            // Log untuk debug

            // 2. Cek tipe purchase
            if ("Aset".equalsIgnoreCase(purchase.getPurchaseType()) && purchase.getPurchaseAsset() != null) {
                var assetResponse = webClient.get()
                    .uri(purchaseUrl + "api/purchase/asset/" + purchase.getPurchaseAsset().getId())
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<AssetTempResponseDTO>>() {})
                    .block();

                return assetResponse != null ? assetResponse.getData() : null;
            }

            // 4. Jika bukan aset, return null
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private PurchaseResponseDTO fetchPurchaseDetailById(String purchaseId, String token) {
        try {
            var purchaseDetailResponse = webClient.get()
                .uri(purchaseUrl + "api/purchase/detail/" + purchaseId)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<PurchaseResponseDTO>>() {})
                .block();

            return purchaseDetailResponse != null ? purchaseDetailResponse.getData() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private List<Lapkeu> fetchPurchaseLapkeu(String token) {
        List<Lapkeu> lapkeuList = new ArrayList<>();
        var purchaseResponse = webClient.get()
            .uri(purchaseUrl + "api/purchase/viewall")
            .headers(headers -> headers.setBearerAuth(token))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<PurchaseResponseDTO>>>() {})
            .block();

        if (purchaseResponse != null && purchaseResponse.getData() != null) {
            for (var purchase : purchaseResponse.getData()) {
                if (purchase.getPurchasePaymentDate() != null) {
                    Lapkeu lapkeu = new Lapkeu();
                    lapkeu.setId(purchase.getPurchaseId());
                    lapkeu.setActivityType(2); // 2 = PURCHASE
                    lapkeu.setPemasukan(0L);
                    lapkeu.setPengeluaran(purchase.getPurchasePrice() != null ? purchase.getPurchasePrice().longValue() : 0L);
                    lapkeu.setPaymentDate(getDateOnly(purchase.getPurchasePaymentDate()));
                    
                    if ("Aset".equalsIgnoreCase(purchase.getPurchaseType())) {
                        var asset = fetchAssetTempById(purchase.getPurchaseId(), token);
                        if (asset != null && asset.getAssetNameString() != null) {
                            lapkeu.setDescription("Pembelian Resource - " + asset.getAssetNameString());
                        } else {
                            lapkeu.setDescription("Pembelian aset (data tidak ditemukan)");
                        }
                    } else if ("Resource".equalsIgnoreCase(purchase.getPurchaseType())) {
                        // Fetch detail purchase untuk dapatkan list resource yang lengkap
                        var purchaseDetail = fetchPurchaseDetailById(purchase.getPurchaseId(), token);
                        List<ResourceTempResponseDTO> resourceList = purchaseDetail != null ? purchaseDetail.getPurchaseResource() : null;

                        if (resourceList != null && !resourceList.isEmpty()) {
                            String barang = resourceList.stream()
                                .map(r -> r.getResourceName())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("-");
                            lapkeu.setDescription("Pembelian Aset - " + barang);
                        } else {
                            lapkeu.setDescription("Pembelian resource (data tidak ditemukan)");
                        }
                    }
                    lapkeuList.add(lapkeu);
                }
            }
        }
        return lapkeuList;
    }

    private List<Lapkeu> fetchMaintenanceLapkeu(String token) {
        List<Lapkeu> lapkeuList = new ArrayList<>();
        var maintenanceResponse = webClient.get()
            .uri(assetUrl + "api/maintenance/all")
            .headers(headers -> headers.setBearerAuth(token))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<MaintenanceResponseDTO>>>() {})
            .block();

        if (maintenanceResponse != null) {
            for (var maintenance : maintenanceResponse.getData()) {
                Lapkeu lapkeu = new Lapkeu();
                lapkeu.setId(String.valueOf(maintenance.getId()));
                lapkeu.setActivityType(3); // 3 = MAINTENANCE
                lapkeu.setPemasukan(0L);
                lapkeu.setPengeluaran(maintenance.getBiaya() != null ? maintenance.getBiaya().longValue() : 0L);
                lapkeu.setPaymentDate(getDateOnly(maintenance.getTanggalMulaiMaintenance()));
                lapkeu.setDescription("Maintenance Aset - "+ maintenance.getPlatNomor());
                lapkeuList.add(lapkeu);
            }
        }
        return lapkeuList;
    }    
}