package gpl.karina.project.restservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import gpl.karina.project.model.Distribution;
import gpl.karina.project.model.Project;
import gpl.karina.project.model.ProjectAssetUsage;
import gpl.karina.project.model.ProjectResourceUsage;
import gpl.karina.project.model.Sell;

import org.springframework.core.ParameterizedTypeReference;

import gpl.karina.project.restdto.AssetUsageDTO;
import gpl.karina.project.restdto.ResourceUsageDTO;
import gpl.karina.project.restdto.fetch.AssetDetailDTO;
import gpl.karina.project.restdto.fetch.ClientDetailDTO;
import gpl.karina.project.restdto.fetch.ResourceDetailDTO;
import gpl.karina.project.restdto.request.ProjectRequestDTO;
import gpl.karina.project.restdto.response.BaseResponseDTO;
import gpl.karina.project.restdto.response.DistributionResponseDTO;
import gpl.karina.project.restdto.response.ProjectResponseWrapperDTO;
import gpl.karina.project.restdto.response.SellResponseDTO;
import gpl.karina.project.restdto.response.listProjectResponseDTO;
import gpl.karina.project.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

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
    private final WebClient.Builder webClientBuilder;

    public ProjectServiceImpl(ProjectRepository projectRepository, WebClient.Builder webClientBuilder,
            HttpServletRequest request) {
        this.projectRepository = projectRepository;
        this.request = request;
        this.webClientBuilder = webClientBuilder;
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

    // private AssetDetailDTO fetchAssetDetailById(String id) {
    //     var response = webClientAsset
    //             .get()
    //             .uri("/api/asset/" + id)
    //             .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
    //             .retrieve()
    //             .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<AssetDetailDTO>>() {
    //             })
    //             .block();
    //     if (response == null || response.getData() == null) {
    //         throw new IllegalArgumentException("Asset not found with id: " + id);
    //     }

    //     return response.getData();
    // }

    private ResourceDetailDTO fetchResourceDetailById(String id) {
        var response = webClientResource
                .get()
                .uri("/api/resource/find/" + id)
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceDetailDTO>>() {
                })
                .block();
        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Resource not found with id: " + id);
        }

        return response.getData();
    }

    private Boolean validateResource(String id) {
        var response = webClientResource
                .get()
                .uri("/api/resource/find/" + id)
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ResourceDetailDTO>>() {
                })
                .block();
        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Resource not found with id: " + id);
        }
        ResourceDetailDTO resourceDetailDTO = response.getData();
        Boolean isValid = false;
        if (resourceDetailDTO.getId() != null) {
            isValid = true;
        }
        return isValid;
    }

    private Boolean validateAsset(String platNomor) {
        var response = webClientAsset
                .get()
                .uri("/api/asset/" + platNomor)
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<AssetDetailDTO>>() {
                })
                .block();
        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Asset not found with platNomor: " + platNomor);
        }
        AssetDetailDTO assetDetailDTO = response.getData();
        String assetId = assetDetailDTO.getPlatNomor();
        Boolean isValid = false;
        if (assetId != null) {
            isValid = true;
        }
        return isValid;
    }

    private Long fetchTodayProjectCount(Date today) {
        List<Project> projectsTodayList = projectRepository.findAll();

        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(today);
        int todayYear = todayCal.get(Calendar.YEAR);
        int todayMonth = todayCal.get(Calendar.MONTH);
        int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);

        Long projectsCountToday = projectsTodayList.stream()
                .filter(project -> {
                    if (project.getCreatedDate() == null)
                        return false;
                    Calendar projectCal = Calendar.getInstance();
                    projectCal.setTime(project.getCreatedDate());
                    return projectCal.get(Calendar.YEAR) == todayYear &&
                            projectCal.get(Calendar.MONTH) == todayMonth &&
                            projectCal.get(Calendar.DAY_OF_MONTH) == todayDay;
                })
                .count();
        return projectsCountToday;
    }

    private listProjectResponseDTO projectToProjectResponseAllDTO(Project project) {
        listProjectResponseDTO projectResponseDTO = new listProjectResponseDTO();
        projectResponseDTO.setId(project.getId());
        projectResponseDTO.setProjectName(project.getProjectName());
        projectResponseDTO.setProjectStartDate(project.getProjectStartDate());
        projectResponseDTO.setProjectEndDate(project.getProjectEndDate());
        projectResponseDTO.setProjectType(project.getProjectType());
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
                dto.setProjectStatus(distributionProject.getProjectStatus());
                dto.setProjectName(distributionProject.getProjectName());
                dto.setProjectClientId(distributionProject.getProjectClientId());
                dto.setProjectDescription(distributionProject.getProjectDescription());
                dto.setProjectDeliveryAddress(distributionProject.getProjectDeliveryAddress());

                // Distribution-specific fields
                dto.setProjectPickupAddress(distributionProject.getProjectPickupAddress());
                dto.setProjectPHLCount(distributionProject.getProjectPHLCount());
                dto.setProjectTotalPemasukkan(distributionProject.getProjectTotalPemasukkan());
                dto.setProjectTotalPengeluaran(distributionProject.getProjectTotalPengeluaran());
                dto.setProjectStartDate(distributionProject.getProjectStartDate());
                dto.setProjectEndDate(distributionProject.getProjectEndDate());

                // Map asset usages
                if (distributionProject.getProjectUseAsset() != null) {
                    List<AssetUsageDTO> assetUsageDTOs = distributionProject.getProjectUseAsset().stream()
                            .map(assetUsage -> {
                                AssetUsageDTO assetDto = new AssetUsageDTO();
                                assetDto.setPlatNomor(assetUsage.getPlatNomor());
                                assetDto.setAssetFuelCost(assetUsage.getAssetFuelCost());
                                assetDto.setAssetUseCost(assetUsage.getAssetUseCost());
                                return assetDto;
                            })
                            .collect(Collectors.toList());
                    dto.setProjectUseAsset(assetUsageDTOs);
                }

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
                return ProjectResponseWrapperDTO.fromSellResponse(dto);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error processing Sell project: " + e.getMessage(), e);
            }

        } else {
            throw new IllegalArgumentException("Unknown project type: " + project.getClass().getSimpleName());
        }
    }

    @Override
    public ProjectResponseWrapperDTO addProject(ProjectRequestDTO projectRequestDTO) throws Exception {
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

            // Handle asset usage
            Long totalPengeluaran = 0L;
            List<ProjectAssetUsage> projectAssetUsages = new ArrayList<>();

            if (projectRequestDTO.getProjectUseAsset() != null) {
                for (AssetUsageDTO assetItem : projectRequestDTO.getProjectUseAsset()) {
                    if (!validateAsset(assetItem.getPlatNomor())) {
                        throw new IllegalArgumentException("Pastikan ID Aset sudah terdaftar dalam sistem");
                    }
                    totalPengeluaran += assetItem.getAssetFuelCost() + assetItem.getAssetUseCost();

                    ProjectAssetUsage projectAssetUsage = new ProjectAssetUsage();
                    projectAssetUsage.setPlatNomor(assetItem.getPlatNomor());
                    projectAssetUsage.setProject(distributionProject);
                    projectAssetUsage.setAssetFuelCost(assetItem.getAssetFuelCost());
                    projectAssetUsage.setAssetUseCost(assetItem.getAssetUseCost());
                    projectAssetUsages.add(projectAssetUsage);
                }
            }
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
                    if (!validateResource(resourceItem.getResourceId())) {
                        throw new IllegalArgumentException("Pastikan ID Resource sudah terdaftar dalam sistem");
                    }

                    ResourceDetailDTO resourceDetail = fetchResourceDetailById(resourceItem.getResourceId());
                    totalPemasukkan += resourceDetail.getResourcePrice();

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
        project.setProjectStatus("Direncanakan");
        project.setProjectDescription(projectRequestDTO.getProjectDescription());
        project.setProjectClientId(projectRequestDTO.getProjectClientId());
        project.setProjectType(projectRequestDTO.getProjectType());
        project.setProjectDeliveryAddress(projectRequestDTO.getProjectDeliveryAddress());
        project.setProjectStartDate(projectRequestDTO.getProjectStartDate());
        project.setProjectEndDate(projectRequestDTO.getProjectEndDate());
        project.setCreatedDate(today);

        // Save the project (polymorphic save)
        Project savedProject = projectRepository.save(project);

        // Return appropriate response
        return projectToProjectResponseDetailDTO(savedProject);
    }

    @Override
    public List<listProjectResponseDTO> getAllProject(
            String idSearch, String projectStatus, String projectType,
            String projectName, String projectClientId, Date projectStartDate,
            Date projectEndDate
    ) throws Exception {

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
                .filter(project -> projectStatus == null || project.getProjectStatus().equalsIgnoreCase(projectStatus))
                .filter(project -> projectType == null || project.getProjectType().toString().equalsIgnoreCase(projectType))
                .filter(project -> projectName == null || project.getProjectName().toLowerCase().contains(projectName.toLowerCase()))
                .filter(project -> projectClientId == null || project.getProjectClientId().toLowerCase().contains(projectClientId.toLowerCase()))
                .filter(project -> projectStartDate == null || !project.getProjectStartDate().before(projectStartDate))
                .filter(project -> adjustedEndDate == null || !project.getProjectEndDate().after(adjustedEndDate))
                .collect(Collectors.toList());

        return filteredProjects.stream()
                .map(this::projectToProjectResponseAllDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponseWrapperDTO updateProjectStatus(String id, String projectStatus) throws Exception {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project tidak ditemukan dengan id: " + id));
        project.setProjectStatus(projectStatus);
        Project updatedProject = projectRepository.save(project);
        return projectToProjectResponseDetailDTO(updatedProject);
    }
}