package gpl.karina.finance.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.finance.report.dto.response.LapkeuResponseDTO;
import gpl.karina.finance.report.dto.response.ProjectResponseDTO;
import gpl.karina.finance.report.dto.response.PurchaseResponseDTO;
import gpl.karina.finance.report.dto.response.MaintenanceResponseDTO;
import gpl.karina.finance.report.model.Lapkeu;
import gpl.karina.finance.report.repository.LapkeuRepository;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class LapkeuServiceImpl implements LapkeuService {

    private final LapkeuRepository lapkeuRepository;
    private final HttpServletRequest request;

    @Value("${project.service.url}")
    private String projectServiceUrl;

    @Value("${purchase.service.url}")
    private String purchaseServiceUrl;

    @Value("${asset.service.url}")
    private String assetServiceUrl;

    private final WebClient webClient = WebClient.create();

    public LapkeuServiceImpl(LapkeuRepository lapkeuRepository, HttpServletRequest request) {
        this.lapkeuRepository = lapkeuRepository;
        this.request = request;
    }

    public String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public List<LapkeuResponseDTO> fetchAllLapkeu() {
        List<Lapkeu> lapkeuList = lapkeuRepository.findAll();
        return lapkeuList.stream()
                .map(l -> new LapkeuResponseDTO(
                        l.getId(), l.getActivityType(), l.getPemasukan(), l.getPengeluaran(), l.getDescription()))
                .collect(Collectors.toList());
    }

    public void syncLapkeuFromAllModules() {
        String token = getTokenFromRequest();
        List<Lapkeu> lapkeuList = new ArrayList<>();

        lapkeuList.addAll(fetchProjectLapkeu(token));
        lapkeuList.addAll(fetchPurchaseLapkeu(token));
        lapkeuList.addAll(fetchMaintenanceLapkeu(token));

        // Simpan ke database (replace/insert sesuai kebutuhan)
        for (Lapkeu lapkeu : lapkeuList) {
            lapkeuRepository.save(lapkeu);
        }
    }

    private List<Lapkeu> fetchProjectLapkeu(String token) {
        List<Lapkeu> lapkeuList = new ArrayList<>();
        var projectResponse = webClient.get()
            .uri(projectServiceUrl + "/api/project/all")
            .headers(headers -> headers.setBearerAuth(token))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<ProjectResponseDTO>>() {})
            .block();

        if (projectResponse != null) {
            for (var project : projectResponse) {
                Lapkeu lapkeu = new Lapkeu();
                lapkeu.setId(project.getId());
                if (project.getProjectType() && project.getProjectPaymentStatus() == 1) {
                    lapkeu.setActivityType(1); // 1 = PENJUALAN
                } else {
                    lapkeu.setActivityType(0); // 0 = DISTRIBUSI
                }
                lapkeu.setPemasukan(project.getProjectTotalPemasukkan());
                lapkeu.setPengeluaran(project.getProjectTotalPengeluaran());
                lapkeu.setDescription(project.getProjectName());
                lapkeuList.add(lapkeu);
            }
        }
        return lapkeuList;
    }

    private List<Lapkeu> fetchPurchaseLapkeu(String token) {
        List<Lapkeu> lapkeuList = new ArrayList<>();
        var purchaseResponse = webClient.get()
            .uri(purchaseServiceUrl + "/api/purchase/all")
            .headers(headers -> headers.setBearerAuth(token))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<PurchaseResponseDTO>>() {})
            .block();

        if (purchaseResponse != null) {
            for (var purchase : purchaseResponse) {
                Lapkeu lapkeu = new Lapkeu();
                lapkeu.setId(purchase.getPurchaseId());
                lapkeu.setActivityType(2); // 2 = PURCHASE
                lapkeu.setPemasukan(0L);
                lapkeu.setPengeluaran(purchase.getPurchasePrice() != null ? purchase.getPurchasePrice().longValue() : 0L);
                // lapkeu.setDescription(purchase.getPurchaseNote()); // diganti nopol / resource apa saja
                lapkeu.setDescription("test success");
                lapkeuList.add(lapkeu);
            }
        }
        return lapkeuList;
    }

    private List<Lapkeu> fetchMaintenanceLapkeu(String token) {
        List<Lapkeu> lapkeuList = new ArrayList<>();
        var maintenanceResponse = webClient.get()
            .uri(assetServiceUrl + "/api/maintenance/all")
            .headers(headers -> headers.setBearerAuth(token))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<MaintenanceResponseDTO>>() {})
            .block();

        if (maintenanceResponse != null) {
            for (var maintenance : maintenanceResponse) {
                Lapkeu lapkeu = new Lapkeu();
                lapkeu.setId(String.valueOf(maintenance.getId()));
                lapkeu.setActivityType(3); // 3 = MAINTENANCE
                lapkeu.setPemasukan(0L);
                lapkeu.setPengeluaran(maintenance.getBiaya() != null ? maintenance.getBiaya().longValue() : 0L);
                lapkeu.setDescription(maintenance.getPlatNomor());
                lapkeuList.add(lapkeu);
            }
        }
        return lapkeuList;
    }

    // Implement the methods defined in the LapkeuService interface here
    
}
