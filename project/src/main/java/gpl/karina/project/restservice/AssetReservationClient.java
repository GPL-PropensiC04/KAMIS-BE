package gpl.karina.project.restservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client for interacting with the Asset Reservation API
 */
@Service
public class AssetReservationClient {

    private static final Logger logger = LoggerFactory.getLogger(AssetReservationClient.class);

    @Value("${project.app.assetUrl}")
    private String assetUrl;

    private WebClient webClientAsset;
    private final WebClient.Builder webClientBuilder;
    private final HttpServletRequest request;

    private static final String PLATNOMORS = "platNomors";
    private static final String PROJECTID = "projectId";
    private static final String STARTDATE = "startDate";
    private static final String ENDDATE = "endDate";
    private static final String EXCLUDEPROJECTID = "excludeProjectId";

    public AssetReservationClient(WebClient.Builder webClientBuilder, HttpServletRequest request) {
        this.webClientBuilder = webClientBuilder;
        this.request = request;
    }

    @PostConstruct
    public void init() {
        logger.info("Asset URL: {}", assetUrl);
        this.webClientAsset = webClientBuilder
                .baseUrl(assetUrl)
                .build();
    }

    private String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Check availability of multiple assets for a specific time period
     */
    public Map<String, Boolean> checkAssetsAvailability(List<String> platNomors, Date startDate, Date endDate) {
        try {
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put(PLATNOMORS, platNomors);
            requestBody.put(STARTDATE, startDate);
            requestBody.put(ENDDATE, endDate);

            // Make the API call
            var response = webClientAsset
                    .post()
                    .uri("/asset/reservations/check-availability")
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new IllegalArgumentException(
                                            "Bad request to asset service: " + body)));
                        }
                        return Mono.error(new IllegalArgumentException(
                                "Client error in asset service: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Asset service is currently unavailable, please try again later"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            if (response == null || response.get("data") == null) {
                logger.error("Invalid response from asset service");
                return new HashMap<>();
            }

            @SuppressWarnings("unchecked")
            Map<String, Boolean> availabilityMap = (Map<String, Boolean>) response.get("data");
            return availabilityMap;
        } catch (WebClientRequestException e) {
            logger.error("Network error checking asset availability: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to connect to asset service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error checking asset availability: {}", e.getMessage());
            throw new IllegalArgumentException("Error checking asset availability: " + e.getMessage());
        }
    }

    /**
     * Check availability of multiple assets for a specific time period, excluding a
     * particular project's reservations
     */
    public Map<String, Boolean> checkAssetsAvailabilityExcludingProject(
            List<String> platNomors, Date startDate, Date endDate, String excludeProjectId) {
        try {
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put(PLATNOMORS, platNomors);
            requestBody.put(STARTDATE, startDate);
            requestBody.put(ENDDATE, endDate);
            requestBody.put(EXCLUDEPROJECTID, excludeProjectId);

            // Make the API call
            var response = webClientAsset
                    .post()
                    .uri("/asset/reservations/check-availability")
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new IllegalArgumentException(
                                            "Bad request to asset service: " + body)));
                        }
                        return Mono.error(new IllegalArgumentException(
                                "Client error in asset service: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Asset service is currently unavailable, please try again later"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            if (response == null || response.get("data") == null) {
                logger.error("Invalid response from asset service");
                return new HashMap<>();
            }

            @SuppressWarnings("unchecked")
            Map<String, Boolean> availabilityMap = (Map<String, Boolean>) response.get("data");
            return availabilityMap;
        } catch (WebClientRequestException e) {
            logger.error("Network error checking asset availability: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to connect to asset service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error checking asset availability: {}", e.getMessage());
            throw new IllegalArgumentException("Error checking asset availability: " + e.getMessage());
        }
    }

    /**
     * Reserve multiple assets for a project
     */
    public void reserveAssets(List<String> platNomors, String projectId, Date startDate, Date endDate) {
        try {
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put(PLATNOMORS, platNomors);
            requestBody.put(PROJECTID, projectId);
            requestBody.put(STARTDATE, startDate);
            requestBody.put(ENDDATE, endDate);

            // Make the API call
            webClientAsset
                    .post()
                    .uri("/asset/reservations/reserve")
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new IllegalArgumentException(
                                            "Failed to reserve assets: " + body)));
                        }
                        return Mono.error(new IllegalArgumentException(
                                "Failed to reserve assets: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Asset service is currently unavailable, please try again later"));
                    })
                    .bodyToMono(Object.class)
                    .block();

            logger.info("Successfully reserved assets for project {}", projectId);
        } catch (WebClientRequestException e) {
            logger.error("Network error reserving assets: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to connect to asset service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error reserving assets: {}", e.getMessage());
            throw new IllegalArgumentException("Error reserving assets: " + e.getMessage());
        }
    }

    /**
     * Update the status of all reservations for a project
     */
    public void updateProjectReservationStatus(String projectId, String status) {
        try {
            // Make the API call
            webClientAsset
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/asset/reservations/project/{projectId}/status")
                            .queryParam("status", status)
                            .build(projectId))
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new IllegalArgumentException(
                                            "Failed to update reservation status: " + body)));
                        }
                        return Mono.error(new IllegalArgumentException(
                                "Failed to update reservation status: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Asset service is currently unavailable, please try again later"));
                    })
                    .bodyToMono(Object.class)
                    .block();

            logger.info("Successfully updated reservation status for project {} to {}", projectId, status);
        } catch (WebClientRequestException e) {
            logger.error("Network error updating reservation status: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to connect to asset service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating reservation status: {}", e.getMessage());
            throw new IllegalArgumentException("Error updating reservation status: " + e.getMessage());
        }
    }
}