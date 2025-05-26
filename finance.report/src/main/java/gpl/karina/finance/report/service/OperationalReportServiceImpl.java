package gpl.karina.finance.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.finance.report.dto.response.ActivityComparisonResponseDTO;
import gpl.karina.finance.report.dto.response.ActivityLineDTO;
import gpl.karina.finance.report.dto.response.BaseResponseDTO;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class OperationalReportServiceImpl implements OperationalReportService {

    @Value("${finance.report.app.projectUrl}")
    private String projectUrl;

    @Value("${finance.report.app.purchaseUrl}")
    private String purchaseUrl;

    private WebClient webClientProject;
    private WebClient webClientPurchase;
    private final HttpServletRequest request;
    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(OperationalReportServiceImpl.class);
    
    public OperationalReportServiceImpl(HttpServletRequest request, WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.request = request;
    }

    @PostConstruct
    public void init() {
        logger.info("Project URL: {}", projectUrl);
        logger.info("Purchase URL: {}", purchaseUrl);

        this.webClientProject = webClientBuilder
                .baseUrl(projectUrl)
                .build();
        this.webClientPurchase = webClientBuilder
                .baseUrl(purchaseUrl)
                .build();
    }

    public String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private List<ActivityLineDTO> fetchPurchaseActivity(String range, String periodType, String status) {
        var response = webClientPurchase
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/purchase/chart/purchase-activity")
                        .queryParam("range", range)
                        .queryParamIfPresent("periodType", Optional.ofNullable(periodType))
                        .queryParam("status", status)
                        .build())
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<ActivityLineDTO>>>() {})
                .block();

        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Data aktivitas pembelian tidak ditemukan");
        }
        return response.getData();
    }

    public List<ActivityLineDTO> fetchProjectActivity(boolean isDistribusi, String range, String periodType, String status) {
        String path = isDistribusi ? "/project/chart/distribusi-activity" : "/project/chart/penjualan-activity";

        var response = webClientProject
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("range", range)
                        .queryParamIfPresent("periodType", Optional.ofNullable(periodType))
                        .queryParam("status", status)
                        .build())
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<ActivityLineDTO>>>() {})
                .block();

        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Data aktivitas " + (isDistribusi ? "distribusi" : "penjualan") + " tidak ditemukan");
        }
        return response.getData();
    }


    @Override
    public List<ActivityComparisonResponseDTO> fetchCombinedActivityLineChart(
            String range, String periodType, String status) {

        List<ActivityLineDTO> pembelian = fetchPurchaseActivity(range, periodType, status);
        List<ActivityLineDTO> penjualan = fetchProjectActivity(false, range, periodType, status);
        List<ActivityLineDTO> distribusi = fetchProjectActivity(true, range, periodType, status);

        Map<String, ActivityComparisonResponseDTO> resultMap = new TreeMap<>();

        for (ActivityLineDTO dto : pembelian) {
            resultMap
                .computeIfAbsent(dto.getPeriod(), ActivityComparisonResponseDTO::new)
                .setPembelianCount(dto.getCount());
        }

        for (ActivityLineDTO dto : penjualan) {
            resultMap
                .computeIfAbsent(dto.getPeriod(), ActivityComparisonResponseDTO::new)
                .setPenjualanCount(dto.getCount());
        }

        for (ActivityLineDTO dto : distribusi) {
            resultMap
                .computeIfAbsent(dto.getPeriod(), ActivityComparisonResponseDTO::new)
                .setDistribusiCount(dto.getCount());
        }

        return new ArrayList<>(resultMap.values());
    }

}
