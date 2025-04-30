package gpl.karina.project.restservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import gpl.karina.project.model.Distribution;
import gpl.karina.project.model.LogProject;
import gpl.karina.project.model.Project;
import gpl.karina.project.model.ProjectAssetUsage;
import gpl.karina.project.model.ProjectResourceUsage;
import gpl.karina.project.model.Sell;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import gpl.karina.project.restdto.AssetUpdateStatusDTO;
import gpl.karina.project.restdto.AssetUsageDTO;
import gpl.karina.project.restdto.ResourceStockUpdateDTO;
import gpl.karina.project.restdto.ResourceUsageDTO;
import gpl.karina.project.restdto.fetch.AssetDetailDTO;
import gpl.karina.project.restdto.fetch.ClientDetailDTO;
import gpl.karina.project.restdto.fetch.ResourceDetailDTO;
import gpl.karina.project.restdto.request.AddProjectRequestDTO;
import gpl.karina.project.restdto.request.UpdateProjectRequestDTO;
import gpl.karina.project.restdto.response.BaseResponseDTO;
import gpl.karina.project.restdto.response.DistributionResponseDTO;
import gpl.karina.project.restdto.response.LogProjectResponseDTO;
import gpl.karina.project.restdto.response.ProjectResponseWrapperDTO;
import gpl.karina.project.restdto.response.SellResponseDTO;
import gpl.karina.project.restdto.response.listProjectResponseDTO;
import gpl.karina.project.security.jwt.JwtUtils;
import gpl.karina.project.repository.LogProjectRepository;
import gpl.karina.project.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    @Value("${project.app.profileUrl}")
    private String profileUrl;
    @Value("${project.app.resourceUrl}")
    private String resourceUrl;
    @Value("${project.app.assetUrl}")
    private String assetUrl;
    private WebClient webClientResource;
    private WebClient webClientAsset;
    private WebClient webClientProfile;

    private final HttpServletRequest request;

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final LogProjectRepository logProjectRepository;
    private final WebClient.Builder webClientBuilder;
    private final JwtUtils jwtUtils;

    public ProjectServiceImpl(ProjectRepository projectRepository, WebClient.Builder webClientBuilder,
            HttpServletRequest request, JwtUtils jwtUtils, LogProjectRepository logProjectRepository) {
        this.projectRepository = projectRepository;
        this.request = request;
        this.webClientBuilder = webClientBuilder;
        this.jwtUtils = jwtUtils;
        this.logProjectRepository = logProjectRepository;
    }

    @PostConstruct
    public void init() {
        logger.info("Profile URL: {}", profileUrl);
        logger.info("Resource URL: {}", resourceUrl);
        logger.info("Asset URL: {}", assetUrl);

        this.webClientProfile = webClientBuilder
                .baseUrl(profileUrl)
                .build();
        this.webClientResource = webClientBuilder
                .baseUrl(resourceUrl)
                .build();

        this.webClientAsset = webClientBuilder
                .baseUrl(assetUrl)
                .build();
    }

    public String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private ClientDetailDTO fetchClientById(String id) {
        var response = webClientProfile
                .get()
                .uri("/api/client/" + id)
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ClientDetailDTO>>() {
                })
                .block();
        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Client not found with id: " + id);
        }
        ClientDetailDTO clientDetailDTO = response.getData();
        return clientDetailDTO;
    }

    private void updateAssetStatus(String platNomor, String status) {
        try {
            var response = webClientAsset
                    .put()
                    .uri("/api/asset/" + platNomor)
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .bodyValue(new AssetUpdateStatusDTO(status))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new IllegalArgumentException(
                                            "Gagal memperbarui status aset: " + body)));
                        }
                        return Mono.error(new IllegalArgumentException(
                                "Gagal memperbarui status aset: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Layanan aset sedang tidak tersedia, silakan coba lagi nanti"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<AssetDetailDTO>>() {
                    })
                    .block();

            if (response == null || response.getData() == null) {
                throw new IllegalArgumentException("Tidak ada respons yang valid dari layanan aset");
            }

            logger.info("Successfully updated asset status to {}", status);
        } catch (WebClientRequestException e) {
            logger.error("Network error updating asset status: {}", e.getMessage());
            throw new IllegalArgumentException("Gagal terhubung ke layanan aset: " + e.getMessage());
        }
    }

    /**
     * Validates an asset by its plate number
     * 
     * @param platNomor Plate number of the asset
     * @return true if valid, false otherwise
     * @throws IllegalArgumentException if asset is invalid
     */

    private void updateAssetStatus(String platNomor, String status) {
        try {
            var response = webClientAsset
                    .put()
                    .uri("/api/asset/" + platNomor)
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .bodyValue(new AssetUpdateStatusDTO(status))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new IllegalArgumentException(
                                            "Gagal memperbarui status aset: " + body)));
                        }
                        return Mono.error(new IllegalArgumentException(
                                "Gagal memperbarui status aset: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Layanan aset sedang tidak tersedia, silakan coba lagi nanti"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<AssetDetailDTO>>() {})
                    .block();
            
            if (response == null || response.getData() == null) {
                throw new IllegalArgumentException("Tidak ada respons yang valid dari layanan aset");
            }
            
            logger.info("Successfully updated asset status to {}", status);
        } catch (WebClientRequestException e) {
            logger.error("Network error updating asset status: {}", e.getMessage());
            throw new IllegalArgumentException("Gagal terhubung ke layanan aset: " + e.getMessage());
        }
    }
    /**
     * Validates an asset by its plate number
     * 
     * @param platNomor Plate number of the asset
     * @return true if valid, false otherwise
     * @throws IllegalArgumentException if asset is invalid
     */

    private Boolean validateAsset(String platNomor) {
        try {
            var response = webClientAsset
                    .get()
                    .uri("/api/asset/" + platNomor)
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            return Mono.error(new IllegalArgumentException(
                                    "Aset dengan nomor plat " + platNomor + " tidak ditemukan"));
                        } else if (clientResponse.statusCode().equals(HttpStatus.FORBIDDEN)) {
                            return Mono.error(new IllegalArgumentException("Tidak memiliki akses untuk melihat aset"));
                        } else {
                            return Mono.error(new IllegalArgumentException(
                                    "Gagal memvalidasi aset: " + clientResponse.statusCode()));
                        }
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Layanan aset sedang tidak tersedia, silakan coba lagi nanti"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<AssetDetailDTO>>() {
                    })
                    .block();

            if (response == null) {
                throw new IllegalArgumentException("Tidak ada respons dari layanan aset");
            }

            if (response.getData() == null) {
                throw new IllegalArgumentException(
                        "Aset dengan nomor plat " + platNomor + " tidak memiliki data yang valid");
            }

            AssetDetailDTO assetDetailDTO = response.getData();
            String assetId = assetDetailDTO.getPlatNomor();

            if (assetId == null || assetId.isEmpty()) {
                throw new IllegalArgumentException("Aset dengan nomor plat " + platNomor + " memiliki ID yang kosong");
            }

            return true;
        } catch (WebClientRequestException e) {
            logger.error("Network error validating asset: {}", e.getMessage());
            throw new IllegalArgumentException("Gagal terhubung ke layanan aset: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Rethrow IllegalArgumentException as is
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error validating asset: {}", e.getMessage());
            throw new IllegalArgumentException("Gagal memvalidasi aset: " + e.getMessage());
        }
    }

    /**
     * Fetches and validates a resource by ID
     * 
     * @param id Resource ID
     * @return Validated ResourceDetailDTO
     * @throws IllegalArgumentException if resource is invalid
     */
    private ResourceDetailDTO fetchAndValidateResource(String id) {
        try {
            var response = webClientResource
                    .get()
                    .uri("/api/resource/find/" + id)
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            return Mono.error(
                                    new IllegalArgumentException("Resource dengan ID " + id + " tidak ditemukan"));
                        } else if (clientResponse.statusCode().equals(HttpStatus.FORBIDDEN)) {
                            return Mono
                                    .error(new IllegalArgumentException("Tidak memiliki akses untuk melihat resource"));
                        } else {
                            return Mono.error(new IllegalArgumentException(
                                    "Gagal memvalidasi resource: " + clientResponse.statusCode()));
                        }
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Layanan resource sEedang tidak tersedia, silakan coba lagi nanti"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceDetailDTO>>() {
                    })
                    .block();

            if (response == null) {
                throw new IllegalArgumentException("Tidak ada respons dari layanan resource");
            }

            if (response.getData() == null) {
                throw new IllegalArgumentException("Resource dengan ID " + id + " tidak memiliki data yang valid");
            }

            ResourceDetailDTO resourceDetailDTO = response.getData();

            if (resourceDetailDTO.getId() == null) {
                throw new IllegalArgumentException("Resource dengan ID " + id + " memiliki ID yang kosong");
            }

            if (resourceDetailDTO.getResourceStock() == 0) {
                throw new IllegalArgumentException("Stock resource dengan ID " + id + " sedang kosong");
            }

            return resourceDetailDTO;
        } catch (WebClientRequestException e) {
            logger.error("Network error validating resource: {}", e.getMessage());
            throw new IllegalArgumentException("Gagal terhubung ke layanan resource: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Rethrow IllegalArgumentException as is
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error validating resource: {}", e.getMessage());
            throw new IllegalArgumentException("Gagal memvalidasi resource: " + e.getMessage());
        }
    }

    /**
     * Deducts stock from a resource
     * 
     * @param resourceId The resource ID
     * @param quantity   The quantity to deduct (must be positive)
     * @throws IllegalArgumentException if the operation fails
     */
    private void deductResourceStock(String resourceId, Integer quantity) {
        try {
            var response = webClientResource
                    .put()
                    .uri("/api/resource/" + resourceId + "/deduct-stock")
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .bodyValue(new ResourceStockUpdateDTO(quantity)).retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new IllegalArgumentException(
                                            "Gagal mengurangi stok resource: " + body)));
                        }
                        return Mono.error(new IllegalArgumentException(
                                "Gagal mengurangi stok resource: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Layanan resource sedang tidak tersedia, silakan coba lagi nanti"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceDetailDTO>>() {
                    })
                    .block();

            if (response == null || response.getData() == null) {
                System.out.println(response);
                throw new IllegalArgumentException("ERR BEGO");
            }

            logger.info("Successfully deducted {} units from resource {}", quantity, resourceId);
        } catch (WebClientRequestException e) {
            logger.error("Network error updating resource stock: {}", e.getMessage());
            throw new IllegalArgumentException("Gagal terhubung ke layanan resource: " + e.getMessage());
        }
    }

    /**
     * Adds stock to a resource
     * 
     * @param resourceId The resource ID
     * @param quantity   The quantity to add (must be positive)
     * @throws IllegalArgumentException if the operation fails
     */
    private void addResourceStock(String resourceId, Integer quantity) {
        try {
            var response = webClientResource
                    .put()
                    .uri("/api/resource/" + resourceId + "/add-stock")
                    .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                    .bodyValue(new ResourceStockUpdateDTO(quantity))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new IllegalArgumentException(
                                            "Gagal menambah stok resource: " + body)));
                        }
                        return Mono.error(new IllegalArgumentException(
                                "Gagal menambah stok resource: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                        return Mono.error(new IllegalArgumentException(
                                "Layanan resource sedang tidak tersedia, silakan coba lagi nanti"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceDetailDTO>>() {
                    })
                    .block();

            if (response == null || response.getData() == null) {
                throw new IllegalArgumentException("Tidak ada respons yang valid dari layanan resource");
            }

            logger.info("Successfully added {} units to resource {}", quantity, resourceId);
        } catch (WebClientRequestException e) {
            logger.error("Network error updating resource stock: {}", e.getMessage());
            throw new IllegalArgumentException("Gagal terhubung ke layanan resource: " + e.getMessage());
        }
    }

    private ResourceDetailDTO fetchResourceDetailById(String id) {
        return fetchAndValidateResource(id);
    }

    private Boolean validateResource(String id) {
        try {
            fetchAndValidateResource(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Long fetchTodayProjectCount(Date today) {
        Long projectsCountToday = projectRepository.countProjectsCreatedOn(today);
        if (projectsCountToday == null) {
            projectsCountToday = 0L;
        }
        return projectsCountToday;
    }

    private LogProject addLog(String action) {
        LogProject log = new LogProject();
        String username = jwtUtils.getUserNameFromJwtToken(getTokenFromRequest());
        log.setUsername(username);
        log.setAction(action);
        Date now = new Date();
        log.setActionDate(now);
        return log; // Just create it, don't save it
    }

    private LogProjectResponseDTO logProjectToLogProjectResponseDTO(LogProject logProject) {
        LogProjectResponseDTO logProjectResponseDTO = new LogProjectResponseDTO();
        logProjectResponseDTO.setId(logProject.getId());
        logProjectResponseDTO.setUser(logProject.getUsername());
        logProjectResponseDTO.setAction(logProject.getAction());
        logProjectResponseDTO.setActionDate(logProject.getActionDate());
        return logProjectResponseDTO;
    }

    private listProjectResponseDTO projectToProjectResponseAllDTO(Project project) {
        listProjectResponseDTO projectResponseDTO = new listProjectResponseDTO();
        projectResponseDTO.setId(project.getId());
        projectResponseDTO.setProjectName(project.getProjectName());
        projectResponseDTO.setProjectStartDate(project.getProjectStartDate());
        projectResponseDTO.setProjectEndDate(project.getProjectEndDate());
        projectResponseDTO.setProjectType(project.getProjectType());
        projectResponseDTO.setProjectPaymentStatus(0);
        projectResponseDTO.setProjectStatus(project.getProjectStatus());
        projectResponseDTO.setProjectClientId(project.getProjectClientId());
        projectResponseDTO.setProjectDescription(project.getProjectDescription());
        projectResponseDTO.setProjectTotalPemasukkan(project.getProjectTotalPemasukkan());

        if (project instanceof Distribution) {
            Distribution distributionProject = (Distribution) project;
            Long totalPengeluaran = distributionProject.getProjectTotalPengeluaran();
            projectResponseDTO.setProjectTotalPengeluaran(totalPengeluaran);
        }

        return projectResponseDTO;
    }

    private ProjectResponseWrapperDTO projectToProjectResponseDetailDTO(Project project) {
        if (project instanceof Distribution) {
            // Cast to Distribution subclass to access specific fields
            try {
                Distribution distributionProject = (Distribution) project;

                DistributionResponseDTO dto = new DistributionResponseDTO();
                dto.setId(distributionProject.getId());
                dto.setProjectType(distributionProject.getProjectType());
                dto.setProjectPaymentStatus(distributionProject.getProjectPaymentStatus());
                dto.setProjectStatus(distributionProject.getProjectStatus());
                dto.setProjectName(distributionProject.getProjectName());
                dto.setProjectClientId(distributionProject.getProjectClientId());
                dto.setProjectDescription(distributionProject.getProjectDescription());
                dto.setProjectDeliveryAddress(distributionProject.getProjectDeliveryAddress());

                // Distribution-specific fields
                dto.setProjectPickupAddress(distributionProject.getProjectPickupAddress());
                dto.setProjectPHLCount(distributionProject.getProjectPHLCount());
                dto.setProjectPHLPay(distributionProject.getProjectPHLPay());
                dto.setProjectTotalPemasukkan(distributionProject.getProjectTotalPemasukkan());
                dto.setProjectTotalPengeluaran(distributionProject.getProjectTotalPengeluaran());
                dto.setProjectStartDate(distributionProject.getProjectStartDate());
                dto.setProjectEndDate(distributionProject.getProjectEndDate());

                // Map asset usages
                if (distributionProject.getProjectUseAsset() != null) {
                    List<AssetUsageDTO> assetUsageDTOs = distributionProject.getProjectUseAsset().stream()
                            .map(assetUsage -> {
                                AssetUsageDTO assetDto = new AssetUsageDTO();
                                assetDto.setTipeAset(assetUsage.getTipeAset());
                                assetDto.setPlatNomor(assetUsage.getPlatNomor());
                                assetDto.setAssetFuelCost(assetUsage.getAssetFuelCost());
                                assetDto.setAssetUseCost(assetUsage.getAssetUseCost());
                                return assetDto;
                            })
                            .collect(Collectors.toList());
                    dto.setProjectUseAsset(assetUsageDTOs);
                }

                List<LogProject> logs = project.getProjectLogs();
                List<LogProjectResponseDTO> logsDTO = new ArrayList<>();
                for (LogProject log : logs) {
                    logsDTO.add(logProjectToLogProjectResponseDTO(log));
                }
                dto.setProjectLogs(logsDTO);

                return ProjectResponseWrapperDTO.fromDistributionResponse(dto);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error processing Distribution project: " + e.getMessage(), e);
            }

        } else if (project instanceof Sell) {
            // Cast to Sell subclass to access specific fields
            try {
                Sell sellProject = (Sell) project;

                SellResponseDTO dto = new SellResponseDTO();
                dto.setId(sellProject.getId());
                dto.setProjectPaymentStatus(sellProject.getProjectPaymentStatus());
                dto.setProjectType(sellProject.getProjectType());
                dto.setProjectStatus(sellProject.getProjectStatus());
                dto.setProjectName(sellProject.getProjectName());
                dto.setProjectDescription(sellProject.getProjectDescription());
                dto.setProjectClientId(sellProject.getProjectClientId());
                dto.setProjectDeliveryAddress(sellProject.getProjectDeliveryAddress());
                dto.setProjectTotalPemasukkan(sellProject.getProjectTotalPemasukkan());
                dto.setProjectStartDate(sellProject.getProjectStartDate());
                dto.setProjectEndDate(sellProject.getProjectEndDate());

                // Map resource usages (Sell-specific)
                if (sellProject.getProjectUseResource() != null) {
                    List<ResourceUsageDTO> resourceUsageDTOs = sellProject.getProjectUseResource().stream()
                            .map(resourceUsage -> {
                                ResourceUsageDTO resourceDto = new ResourceUsageDTO();
                                resourceDto.setResourceId(resourceUsage.getResourceId());
                                resourceDto.setResourceStockUsed(resourceUsage.getQuantityUsed());
                                resourceDto.setSellPrice(resourceUsage.getSellPrice());
                                return resourceDto;
                            })
                            .collect(Collectors.toList());
                    dto.setProjectUseResource(resourceUsageDTOs);
                }

                List<LogProject> logs = project.getProjectLogs();
                List<LogProjectResponseDTO> logsDTO = new ArrayList<>();
                for (LogProject log : logs) {
                    logsDTO.add(logProjectToLogProjectResponseDTO(log));
                }
                dto.setProjectLogs(logsDTO);

                return ProjectResponseWrapperDTO.fromSellResponse(dto);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error processing Sell project: " + e.getMessage(), e);
            }

        } else {
            throw new IllegalArgumentException("Unknown project type: " + project.getClass().getSimpleName());
        }
    }

    @Override
    public ProjectResponseWrapperDTO addProject(AddProjectRequestDTO projectRequestDTO) throws Exception {
        // Validate client
        if (fetchClientById(projectRequestDTO.getProjectClientId()).getId() == null) {
            throw new IllegalArgumentException("Pastikan ID Klien sudah terdaftar dalam sistem");
        }

        // Common setup
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);

        Long projectNumber = fetchTodayProjectCount(today) + 1;
        String todayFormatted = String.format("%02d", calendar.get(Calendar.YEAR) % 100)
                + String.format("%02d", calendar.get(Calendar.MONTH) + 1)
                + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

        Project project;

        if (projectRequestDTO.getProjectType()) {
            // Create a Distribution object
            Distribution distributionProject = new Distribution();

            // Generate ID with D prefix
            String id = "D" + String.format("%03d", projectNumber) + todayFormatted;
            distributionProject.setId(id);

            // Set distribution-specific properties
            distributionProject.setProjectPickupAddress(projectRequestDTO.getProjectPickupAddress());
            distributionProject.setProjectPHLCount(projectRequestDTO.getProjectPHLCount());
            distributionProject.setProjectPHLPay(projectRequestDTO.getProjectPHLPay());

            // Handle asset usage
            Long totalPengeluaran = 0L;
            List<ProjectAssetUsage> projectAssetUsages = new ArrayList<>();

            if (projectRequestDTO.getProjectUseAsset() != null) {
                for (AssetUsageDTO assetItem : projectRequestDTO.getProjectUseAsset()) {
                    if (!validateAsset(assetItem.getPlatNomor())) {
                        throw new IllegalArgumentException("Pastikan ID Aset sudah terdaftar dalam sistem");
                    }
                    totalPengeluaran += assetItem.getAssetFuelCost() + assetItem.getAssetUseCost();
                    updateAssetStatus(assetItem.getPlatNomor(), "Dalam Proyek");
                    ProjectAssetUsage projectAssetUsage = new ProjectAssetUsage();
                    projectAssetUsage.setPlatNomor(assetItem.getPlatNomor());
                    projectAssetUsage.setProject(distributionProject);
                    projectAssetUsage.setAssetFuelCost(assetItem.getAssetFuelCost());
                    projectAssetUsage.setAssetUseCost(assetItem.getAssetUseCost());
                    System.out.println(assetItem.getTipeAset() + "TEST");
                    projectAssetUsage.setTipeAset(assetItem.getTipeAset());
                    projectAssetUsages.add(projectAssetUsage);
                }
            }
            totalPengeluaran += projectRequestDTO.getProjectPHLPay() * projectRequestDTO.getProjectPHLCount();
            distributionProject.setProjectTotalPemasukkan(projectRequestDTO.getProjectTotalPemasukkan());
            distributionProject.setProjectUseAsset(projectAssetUsages);
            distributionProject.setProjectTotalPengeluaran(totalPengeluaran);

            // Set the project reference
            project = distributionProject;

        } else {
            // Create a Sell object
            Sell sellProject = new Sell();

            // Generate ID with P prefix
            String id = "P" + String.format("%03d", projectNumber) + todayFormatted;
            sellProject.setId(id);

            // Handle resource usage
            Long totalPemasukkan = 0L;
            List<ProjectResourceUsage> projectResourceUsages = new ArrayList<>();

            if (projectRequestDTO.getProjectUseResource() != null) {
                for (ResourceUsageDTO resourceItem : projectRequestDTO.getProjectUseResource()) {
                    validateResource(resourceItem.getResourceId());

                    ResourceDetailDTO resourceDetail = fetchResourceDetailById(resourceItem.getResourceId());
                    totalPemasukkan += resourceDetail.getResourcePrice() * resourceItem.getResourceStockUsed();
                    // Deduct resource stock
                    if (resourceItem.getResourceStockUsed() > 0) {
                        deductResourceStock(resourceItem.getResourceId(), resourceItem.getResourceStockUsed());
                    } else {
                        throw new IllegalArgumentException("Jumlah resource yang digunakan tidak boleh kurang dari 0");
                    }
                    ProjectResourceUsage projectResourceUsage = new ProjectResourceUsage();
                    projectResourceUsage.setResourceId(resourceItem.getResourceId());
                    projectResourceUsage.setSellPrice(resourceDetail.getResourcePrice());
                    projectResourceUsage.setQuantityUsed(resourceItem.getResourceStockUsed());
                    projectResourceUsage.setProject(sellProject);
                    projectResourceUsages.add(projectResourceUsage);
                }
            }

            sellProject.setProjectUseResource(projectResourceUsages);
            sellProject.setProjectTotalPemasukkan(totalPemasukkan);

            // Set the project reference
            project = sellProject;
        }

        // Set common properties for all project types
        project.setProjectName(projectRequestDTO.getProjectName());
        project.setProjectStatus(0);
        project.setProjectPaymentStatus(0);
        project.setProjectDescription(projectRequestDTO.getProjectDescription());
        project.setProjectClientId(projectRequestDTO.getProjectClientId());
        project.setProjectType(projectRequestDTO.getProjectType());
        project.setProjectDeliveryAddress(projectRequestDTO.getProjectDeliveryAddress());
        project.setProjectStartDate(projectRequestDTO.getProjectStartDate());
        project.setProjectEndDate(projectRequestDTO.getProjectEndDate());
        project.setCreatedDate(today);
        project.setProjectLogs(new ArrayList<>());

        LogProject newLog = addLog("Menambahkan " + project.getId());
        project.getProjectLogs().add(newLog);

        // Save the project (polymorphic save)
        Project savedProject = projectRepository.save(project);

        // Return appropriate response
        return projectToProjectResponseDetailDTO(savedProject);
    }

    @Override
    public ProjectResponseWrapperDTO updateProject(UpdateProjectRequestDTO updateProjectRequestDTO) throws Exception {
        // Find the existing project by ID
        Project project = projectRepository.findById(updateProjectRequestDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Project tidak ditemukan dengan id: " + updateProjectRequestDTO.getId()));

        // Check if project can be updated (not completed or cancelled)
        if (project.getProjectStatus() == 2 || project.getProjectStatus() == 3) {
            throw new IllegalArgumentException("Proyek yang sudah selesai atau batal tidak dapat diubah.");
        }

        StringBuilder logBuilder = new StringBuilder("Memperbarui Proyek:\n");
        boolean hasChange = false;

        // Cek perubahan deskripsi
        if (updateProjectRequestDTO.getProjectDescription() != null &&
                !Objects.equals(updateProjectRequestDTO.getProjectDescription(), project.getProjectDescription())) {
            logBuilder.append("  - Mengubah deskripsi menjadi: ")
                    .append(updateProjectRequestDTO.getProjectDescription()).append("\n");
            hasChange = true;
        }

        // Cek perubahan alamat pengiriman
        if (updateProjectRequestDTO.getProjectDeliveryAddress() != null &&
                !Objects.equals(updateProjectRequestDTO.getProjectDeliveryAddress(),
                        project.getProjectDeliveryAddress())) {
            logBuilder.append("  - Mengubah alamat pengiriman menjadi: ")
                    .append(updateProjectRequestDTO.getProjectDeliveryAddress()).append("\n");
            hasChange = true;
        }

        // Cek perubahan tanggal mulai
        if (updateProjectRequestDTO.getProjectStartDate() != null &&
                !Objects.equals(updateProjectRequestDTO.getProjectStartDate(), project.getProjectStartDate())) {
            logBuilder.append("  - Mengubah tanggal mulai menjadi: ")
                    .append(updateProjectRequestDTO.getProjectStartDate()).append("\n");
            hasChange = true;
        }

        // Cek perubahan tanggal akhir
        if (updateProjectRequestDTO.getProjectEndDate() != null &&
                !Objects.equals(updateProjectRequestDTO.getProjectEndDate(), project.getProjectEndDate())) {
            logBuilder.append("  - Mengubah tanggal selesai menjadi: ")
                    .append(updateProjectRequestDTO.getProjectEndDate()).append("\n");
            hasChange = true;
        }

        // Cek perubahan untuk Distribution
        if (project instanceof Distribution distribution) {
            if (updateProjectRequestDTO.getProjectPickupAddress() != null &&
                    !Objects.equals(updateProjectRequestDTO.getProjectPickupAddress(),
                            distribution.getProjectPickupAddress())) {
                logBuilder.append("  - Mengubah alamat penjemputan menjadi: ")
                        .append(updateProjectRequestDTO.getProjectPickupAddress()).append("\n");
                hasChange = true;
            }

            if (updateProjectRequestDTO.getProjectPHLCount() != null &&
                    !Objects.equals(updateProjectRequestDTO.getProjectPHLCount(), distribution.getProjectPHLCount())) {
                logBuilder.append("  - Mengubah jumlah PHL menjadi: ")
                        .append(updateProjectRequestDTO.getProjectPHLCount()).append("\n");
                hasChange = true;
            }

            if (updateProjectRequestDTO.getProjectPHLPay() != null &&
                    !Objects.equals(updateProjectRequestDTO.getProjectPHLPay(), distribution.getProjectPHLPay())) {
                logBuilder.append("  - Mengubah gaji PHL menjadi: ").append(updateProjectRequestDTO.getProjectPHLPay())
                        .append("\n");
                hasChange = true;
            }

            if (updateProjectRequestDTO.getProjectTotalPemasukkan() != null &&
                    !Objects.equals(updateProjectRequestDTO.getProjectTotalPemasukkan(),
                            distribution.getProjectTotalPemasukkan())) {
                logBuilder.append("  - Mengubah total pemasukkan menjadi: ")
                        .append(updateProjectRequestDTO.getProjectTotalPemasukkan()).append("\n");
                hasChange = true;
            }

            if (updateProjectRequestDTO.getProjectUseAsset() != null &&
                    hasAssetListChanged(distribution.getProjectUseAsset(),
                            updateProjectRequestDTO.getProjectUseAsset())) {
                logBuilder.append("  - Total aset yang digunakan setelah perubahan: ")
                        .append(updateProjectRequestDTO.getProjectUseAsset().size())
                        .append(" item\n");
                hasChange = true;
            }
        }

        // Cek perubahan untuk Sell
        if (project instanceof Sell sell) {
            if (updateProjectRequestDTO.getProjectTotalPemasukkan() != null &&
                    !Objects.equals(updateProjectRequestDTO.getProjectTotalPemasukkan(),
                            sell.getProjectTotalPemasukkan())) {
                logBuilder.append("  - Mengubah total pemasukkan menjadi: ")
                        .append(updateProjectRequestDTO.getProjectTotalPemasukkan()).append("\n");
                hasChange = true;
            }

            if (updateProjectRequestDTO.getProjectUseResource() != null &&
                    hasResourceListChanged(sell.getProjectUseResource(),
                            updateProjectRequestDTO.getProjectUseResource())) {
                logBuilder.append("  - Total resource yang digunakan setelah perubahan: ")
                        .append(updateProjectRequestDTO.getProjectUseResource().size())
                        .append(" item\n");
                hasChange = true;
            }

        }

        if (!hasChange) {
            logBuilder.append("  - Tidak ada perubahan signifikan\n");
        }

        if (project instanceof Distribution) {
            Distribution distributionProject = (Distribution) project;

            // Update distribution-specific properties
            if (updateProjectRequestDTO.getProjectPickupAddress() != null) {
                distributionProject.setProjectPickupAddress(updateProjectRequestDTO.getProjectPickupAddress());
            }

            if (updateProjectRequestDTO.getProjectPHLCount() != null) {
                distributionProject.setProjectPHLCount(updateProjectRequestDTO.getProjectPHLCount());
            }

            if (updateProjectRequestDTO.getProjectPHLPay() != null) {
                distributionProject.setProjectPHLPay(updateProjectRequestDTO.getProjectPHLPay());
            }

            if (updateProjectRequestDTO.getProjectTotalPemasukkan() != null) {
                distributionProject.setProjectTotalPemasukkan(updateProjectRequestDTO.getProjectTotalPemasukkan());
            }

            // Handle asset updates if provided
            if (updateProjectRequestDTO.getProjectUseAsset() != null) {
                // Check if assets have actually changed
                if (hasAssetListChanged(distributionProject.getProjectUseAsset(),
                        updateProjectRequestDTO.getProjectUseAsset())) {
                    // Calculate new expenses
                    Long totalPengeluaran = 0L;

                    // Remove existing asset usages
                    if (distributionProject.getProjectUseAsset() != null
                            && !distributionProject.getProjectUseAsset().isEmpty()) {
                        // Release assets that were in use
                        for (ProjectAssetUsage assetUsage : distributionProject.getProjectUseAsset()) {
                            updateAssetStatus(assetUsage.getPlatNomor(), "Aktif");
                        }
                        distributionProject.getProjectUseAsset().clear();
                    } else {
                        distributionProject.setProjectUseAsset(new ArrayList<>());
                    }

                    // Add new asset usages
                    for (AssetUsageDTO assetItem : updateProjectRequestDTO.getProjectUseAsset()) {
                        if (!validateAsset(assetItem.getPlatNomor())) {
                            throw new IllegalArgumentException("Pastikan ID Aset sudah terdaftar dalam sistem");
                        }

                        totalPengeluaran += assetItem.getAssetFuelCost() + assetItem.getAssetUseCost();
                        updateAssetStatus(assetItem.getPlatNomor(), "Dalam Proyek");

                        ProjectAssetUsage projectAssetUsage = new ProjectAssetUsage();
                        projectAssetUsage.setPlatNomor(assetItem.getPlatNomor());
                        projectAssetUsage.setProject(distributionProject);
                        projectAssetUsage.setAssetFuelCost(assetItem.getAssetFuelCost());
                        projectAssetUsage.setAssetUseCost(assetItem.getAssetUseCost());
                        projectAssetUsage.setTipeAset(assetItem.getTipeAset());
                        distributionProject.getProjectUseAsset().add(projectAssetUsage);
                    }

                    // Update total expenses
                    distributionProject.setProjectTotalPengeluaran(totalPengeluaran);

                    logger.info("Assets updated for project {}", distributionProject.getId());
                } else {
                    logger.info("No changes in assets for project {}", distributionProject.getId());
                }
            } else if (updateProjectRequestDTO.getProjectTotalPengeluaran() != null) {
                distributionProject.setProjectTotalPengeluaran(updateProjectRequestDTO.getProjectTotalPengeluaran());
            }

            // Set the project reference
            project = distributionProject;

        } else if (project instanceof Sell) {
            Sell sellProject = (Sell) project;

            if (updateProjectRequestDTO.getProjectTotalPemasukkan() != null) {
                sellProject.setProjectTotalPemasukkan(updateProjectRequestDTO.getProjectTotalPemasukkan());
            }

            // Handle resource updates if provided
            if (updateProjectRequestDTO.getProjectUseResource() != null) {
                // Check if resources have actually changed
                if (hasResourceListChanged(sellProject.getProjectUseResource(),
                        updateProjectRequestDTO.getProjectUseResource())) {
                    // Calculate new income
                    Long totalPemasukkan = 0L;

                    // Return stock for existing resources
                    if (sellProject.getProjectUseResource() != null && !sellProject.getProjectUseResource().isEmpty()) {
                        for (ProjectResourceUsage resourceUsage : sellProject.getProjectUseResource()) {
                            addResourceStock(resourceUsage.getResourceId(), resourceUsage.getQuantityUsed());
                        }
                        sellProject.getProjectUseResource().clear();
                    } else {
                        sellProject.setProjectUseResource(new ArrayList<>());
                    }

                    // Process new resources
                    for (ResourceUsageDTO resourceItem : updateProjectRequestDTO.getProjectUseResource()) {
                        validateResource(resourceItem.getResourceId());

                        ResourceDetailDTO resourceDetail = fetchResourceDetailById(resourceItem.getResourceId());
                        totalPemasukkan += resourceDetail.getResourcePrice() * resourceItem.getResourceStockUsed();

                        // Deduct resource stock
                        if (resourceItem.getResourceStockUsed() > 0) {
                            deductResourceStock(resourceItem.getResourceId(), resourceItem.getResourceStockUsed());
                        } else {
                            throw new IllegalArgumentException(
                                    "Jumlah resource yang digunakan tidak boleh kurang dari 0");
                        }

                        ProjectResourceUsage projectResourceUsage = new ProjectResourceUsage();
                        projectResourceUsage.setResourceId(resourceItem.getResourceId());
                        projectResourceUsage.setSellPrice(resourceDetail.getResourcePrice());
                        projectResourceUsage.setQuantityUsed(resourceItem.getResourceStockUsed());
                        projectResourceUsage.setProject(sellProject);
                        sellProject.getProjectUseResource().add(projectResourceUsage);
                    }

                    // Update total income based on the new resource usages
                    sellProject.setProjectTotalPemasukkan(totalPemasukkan);

                    logger.info("Resources updated for project {}", sellProject.getId());
                } else {
                    logger.info("No changes in resources for project {}", sellProject.getId());
                }
            }

            // Set the project reference
            project = sellProject;
        }

        // Update common properties for all project types
        if (updateProjectRequestDTO.getProjectDescription() != null) {
            project.setProjectDescription(updateProjectRequestDTO.getProjectDescription());
        }

        if (updateProjectRequestDTO.getProjectDeliveryAddress() != null) {
            project.setProjectDeliveryAddress(updateProjectRequestDTO.getProjectDeliveryAddress());
        }

        if (updateProjectRequestDTO.getProjectStartDate() != null) {
            project.setProjectStartDate(updateProjectRequestDTO.getProjectStartDate());
        }

        if (updateProjectRequestDTO.getProjectEndDate() != null) {
            project.setProjectEndDate(updateProjectRequestDTO.getProjectEndDate());
        }

        LogProject newLog = addLog(logBuilder.toString());
        project.getProjectLogs().add(newLog);
        System.out.println(logBuilder.toString());
        // Save the updated project
        Project updatedProject = projectRepository.save(project);

        // Return appropriate response
        return projectToProjectResponseDetailDTO(updatedProject);
    }

    /**
     * Checks if the asset list has changed from what's currently in the database
     */
    private boolean hasAssetListChanged(List<ProjectAssetUsage> currentAssets, List<AssetUsageDTO> newAssets) {
        // If current is null/empty but new is not, there's a change
        if ((currentAssets == null || currentAssets.isEmpty()) &&
                (newAssets != null && !newAssets.isEmpty())) {
            return true;
        }

        // If new is null/empty but current is not, there's a change
        if ((newAssets == null || newAssets.isEmpty()) &&
                (currentAssets != null && !currentAssets.isEmpty())) {
            return true;
        }

        // If both are empty or null, no change
        if ((currentAssets == null || currentAssets.isEmpty()) &&
                (newAssets == null || newAssets.isEmpty())) {
            return false;
        }

        // If counts differ, there's a change
        if (currentAssets.size() != newAssets.size()) {
            return true;
        }

        // Create a map of existing assets for efficient comparison
        Map<String, ProjectAssetUsage> existingAssetMap = new HashMap<>();
        for (ProjectAssetUsage asset : currentAssets) {
            existingAssetMap.put(asset.getPlatNomor(), asset);
        }

        // Check if all new assets match existing ones
        for (AssetUsageDTO newAsset : newAssets) {
            ProjectAssetUsage currentAsset = existingAssetMap.get(newAsset.getPlatNomor());

            // If this asset doesn't exist or has different properties, there's a change
            if (currentAsset == null ||
                    !Objects.equals(currentAsset.getAssetFuelCost(), newAsset.getAssetFuelCost())
                    || !Objects.equals(currentAsset.getAssetUseCost(), newAsset.getAssetUseCost())) {
                return true;
            }
        }

        // No changes detected
        return false;
    }

    /**
     * Checks if the resource list has changed from what's currently in the database
     */
    private boolean hasResourceListChanged(List<ProjectResourceUsage> currentResources,
            List<ResourceUsageDTO> newResources) {
        // If current is null/empty but new is not, there's a change
        if ((currentResources == null || currentResources.isEmpty()) &&
                (newResources != null && !newResources.isEmpty())) {
            return true;
        }

        // If new is null/empty but current is not, there's a change
        if ((newResources == null || newResources.isEmpty()) &&
                (currentResources != null && !currentResources.isEmpty())) {
            return true;
        }

        // If both are empty or null, no change
        if ((currentResources == null || currentResources.isEmpty()) &&
                (newResources == null || newResources.isEmpty())) {
            return false;
        }

        // If counts differ, there's a change
        if (currentResources.size() != newResources.size()) {
            return true;
        }

        // Create a map of existing resources for efficient comparison
        Map<String, ProjectResourceUsage> existingResourceMap = new HashMap<>();
        for (ProjectResourceUsage resource : currentResources) {
            existingResourceMap.put(resource.getResourceId(), resource);
        }

        // Check if all new resources match existing ones
        for (ResourceUsageDTO newResource : newResources) {
            ProjectResourceUsage currentResource = existingResourceMap.get(newResource.getResourceId());

            // If this resource doesn't exist or has different properties, there's a change
            if (currentResource == null ||
                    !Objects.equals(currentResource.getQuantityUsed(), newResource.getResourceStockUsed()) ||
                    !Objects.equals(currentResource.getSellPrice(), newResource.getSellPrice())) {
                return true;
            }
        }

        // No changes detected
        return false;
    }

    @Override
    public List<listProjectResponseDTO> getAllProject(
            String idSearch, String projectStatus, String projectType,
            String projectName, String projectClientId, Date projectStartDate,
            Date projectEndDate) throws Exception {

        final Date adjustedEndDate;
        if (projectEndDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(projectEndDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            adjustedEndDate = calendar.getTime();
        } else {
            adjustedEndDate = null;
        }

        List<Project> projects = projectRepository.findAll();

        List<Project> filteredProjects = projects.stream()
                .filter(project -> idSearch == null || project.getId().toLowerCase().contains(idSearch.toLowerCase()))
                .filter(project -> projectStatus == null
                        || String.valueOf(project.getProjectStatus()).equalsIgnoreCase(projectStatus))
                .filter(project -> projectType == null
                        || project.getProjectType().toString().equalsIgnoreCase(projectType))
                .filter(project -> projectName == null
                        || project.getProjectName().toLowerCase().contains(projectName.toLowerCase()))
                .filter(project -> projectClientId == null
                        || project.getProjectClientId().toLowerCase().contains(projectClientId.toLowerCase()))
                .filter(project -> projectStartDate == null || !project.getProjectStartDate().before(projectStartDate))
                .filter(project -> adjustedEndDate == null || !project.getProjectEndDate().after(adjustedEndDate))
                .collect(Collectors.toList());

        return filteredProjects.stream()
                .map(this::projectToProjectResponseAllDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponseWrapperDTO getProjectById(String id) throws Exception {
        return projectRepository.findById(id)
                .map(this::projectToProjectResponseDetailDTO)
                .orElseThrow(() -> new IllegalArgumentException("Project tidak ditemukan dengan id: " + id));
    }

    @Override
    public ProjectResponseWrapperDTO updateProjectStatus(String id, Integer newStatus) throws Exception {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project tidak ditemukan dengan id: " + id));
        Integer currentStatus = project.getProjectStatus();

        // Tidak bisa update jika sudah selesai (2) atau batal (3)
        if (currentStatus == 2 || currentStatus == 3) {
            throw new IllegalArgumentException("Status proyek sudah selesai atau batal, tidak bisa diubah lagi.");
        }

        // Tidak bisa kembali ke 0 (Direncanakan) dari status 1 (Dilaksanakan)
        if (currentStatus == 1 && newStatus == 0) {
            throw new IllegalArgumentException(
                    "Status proyek tidak bisa dikembalikan ke 'Direncanakan' dari 'Dilaksanakan'.");
        }

        // Status batal (3) hanya bisa dari 0 (Direncanakan) atau 1 (Dilaksanakan),
        // tidak dari 2 (Selesai)
        if (newStatus == 3) {
            if (currentStatus == 2) {
                throw new IllegalArgumentException("Status proyek tidak bisa dibatalkan jika sudah selesai.");
            } else if (currentStatus == 3) {
                throw new IllegalArgumentException("Status proyek sudah dibatalkan, tidak bisa diubah lagi.");
            }
        }

        // Validasi selesai: tidak bisa kembali ke status sebelumnya
        if (currentStatus == 0 && newStatus == 2) {
            throw new IllegalArgumentException(
                    "Status proyek tidak bisa langsung menjadi 'Selesai' dari 'Direncanakan'.");
        }

        project.setProjectStatus(newStatus);

        String statusText = "";
        if (newStatus == 1) {
            statusText = "Dilaksanakan";
        } else if (newStatus == 2) {
            statusText = "Selesai";
            project.setProjectEndDate(new Date());

        } else if (newStatus == 3) {
            statusText = "Batal";
            project.setProjectEndDate(new Date());

        }

        LogProject newLog = addLog("Mengubah Status menjadi " + statusText);

        project.getProjectLogs().add(newLog);

        Project updatedProject = projectRepository.save(project);
        return projectToProjectResponseDetailDTO(updatedProject);
    }

    @Override
    public ProjectResponseWrapperDTO updateProjectPayment(String id, Integer projectPaymentStatus) throws Exception {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project tidak ditemukan dengan id: " + id));

        Integer currentPaymentStatus = project.getProjectPaymentStatus();
        Integer currentProjectStatus = project.getProjectStatus();

        // Jika ingin update ke pengembalian (2)
        if (projectPaymentStatus == 2) {
            // Hanya boleh jika status proyek sudah batal (3) dan sebelumnya sudah dibayar
            // (1)
            if (currentProjectStatus != null && currentProjectStatus == 3) {
                if (currentPaymentStatus != null && currentPaymentStatus == 1) {
                    // Boleh update ke pengembalian
                } else {
                    throw new IllegalArgumentException("Pengembalian hanya bisa dilakukan jika proyek sudah dibayar.");
                }
            } else {
                throw new IllegalArgumentException("Pengembalian hanya bisa dilakukan jika proyek sudah dibatalkan.");
            }
        } else {
            // Jika sudah dibayar, tidak bisa diubah lagi ke status lain selain pengembalian
            if (currentPaymentStatus != null && currentPaymentStatus == 1) {
                throw new IllegalArgumentException(
                        "Proyek sudah dibayar, tidak dapat diubah status pembayarannya kecuali ke pengembalian.");
            }
        }

        project.setProjectPaymentStatus(projectPaymentStatus);

        LogProject newLog = addLog("Mengkonfirmasi status pembayaran telah selesai");
        project.getProjectLogs().add(newLog);

        Project updatedProject = projectRepository.save(project);
        return projectToProjectResponseDetailDTO(updatedProject);
    }
}