package gpl.karina.project.restservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import gpl.karina.project.model.Project;
import org.springframework.core.ParameterizedTypeReference;

import gpl.karina.project.restdto.AssetDetailDTO;
import gpl.karina.project.restdto.ClientDetailDTO;
import gpl.karina.project.restdto.ResourceDetailDTO;
import gpl.karina.project.restdto.request.ProjectRequestDTO;

import gpl.karina.project.restdto.response.BaseResponseDTO;
import gpl.karina.project.restdto.response.ProjectResponseDTO;
import gpl.karina.project.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.var;

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

    private Long fetchAssetById(String id) {
        var response = webClientAsset
                .get()
                .uri("/api/asset/" + id)
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<Long>>() {
                })
                .block();
        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Asset not found with id: " + id);
        }
        Long assetId = response.getData();
        return assetId;
    }
    private Long fetchResourceById(String id) {
        var response = webClientResource
                .get()
                .uri("/api/resource/" + id)
                .headers(headers -> headers.setBearerAuth(getTokenFromRequest()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<Long>>() {
                })
                .block();
        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Resource not found with id: " + id);
        }
        Long resourceId = response.getData();
        return resourceId;
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
                    if (project.getCreatedDate() == null) return false;
                    Calendar projectCal = Calendar.getInstance();
                    projectCal.setTime(project.getCreatedDate());
                    return projectCal.get(Calendar.YEAR) == todayYear &&
                           projectCal.get(Calendar.MONTH) == todayMonth &&
                           projectCal.get(Calendar.DAY_OF_MONTH) == todayDay;
                })
                .count();
        return projectsCountToday;
    }

    private ProjectResponseDTO projectToProjectResponseAllDTO(Project project) {
        ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();
        projectResponseDTO.setId(project.getId());
        projectResponseDTO.setProjectName(project.getProjectName());
        projectResponseDTO.setProjectStartDate(project.getProjectStartDate());
        projectResponseDTO.setProjectEndDate(project.getProjectEndDate());
        projectResponseDTO.setProjectType(project.getProjectType());
        projectResponseDTO.setProjectStatus(project.getProjectStatus());
        projectResponseDTO.setProjectClientId(project.getProjectClientId());
        return projectResponseDTO;
    }

    private ProjectResponseDTO projectToProjectResponseDetailDTO(Project project) {
        ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();
        projectResponseDTO.setId(project.getId());
        projectResponseDTO.setProjectName(project.getProjectName());
        projectResponseDTO.setProjectStartDate(project.getProjectStartDate());
        projectResponseDTO.setProjectEndDate(project.getProjectEndDate());
        projectResponseDTO.setProjectType(project.getProjectType());
        projectResponseDTO.setProjectStatus(project.getProjectStatus());
        projectResponseDTO.setProjectClientId(project.getProjectClientId());
        projectResponseDTO.setProjectDescription(project.getProjectDescription());
        projectResponseDTO.setProjectDeliveryAddress(project.getProjectDeliveryAddress());
        projectResponseDTO.setProjectPickupAddress(project.getProjectPickupAddress());
        projectResponseDTO.setProjectUseAsset(project.getProjectUseAsset());
        projectResponseDTO.setProjectUseResource(project.getProjectUseResource());
        return projectResponseDTO;
    }

    @Override
    public ProjectResponseDTO addProject(ProjectRequestDTO projectRequestDTO) throws Exception {
        if (fetchClientById(projectRequestDTO.getProjectClientId()).getId() == null) {
            throw new IllegalArgumentException("Pastikan ID Klien sudah terdaftar dalam sistem");
        }
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        String id = "";
        Project newProject = new Project();
        Long projectNumber = fetchTodayProjectCount(today) + 1;
        String todayFormatted = String.format("%02d", calendar.get(Calendar.YEAR) % 100)
                + String.format("%02d", calendar.get(Calendar.MONTH) + 1)
                + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        newProject.setProjectName(projectRequestDTO.getProjectName());
        newProject.setProjectStatus("Direncanakan");
        newProject.setProjectDescription(projectRequestDTO.getProjectDescription());
        newProject.setProjectClientId(projectRequestDTO.getProjectClientId());
        newProject.setProjectType(projectRequestDTO.getProjectType());
        if (newProject.getProjectType()) {// Distribusi
            if (projectRequestDTO.getProjectUseAsset() != null) {
                for (String assetId : projectRequestDTO.getProjectUseAsset()) {
                    if (!validateAsset(assetId)) {
                        throw new IllegalArgumentException("Pastikan ID Aset sudah terdaftar dalam sistem");
                    }
                }
            }
            id = "D" + String.format("%03d", projectNumber) + todayFormatted;
            newProject.setId(id);
            newProject.setProjectDeliveryAddress(projectRequestDTO.getProjectDeliveryAddress());
            newProject.setProjectPickupAddress(projectRequestDTO.getProjectPickupAddress());
            newProject.setProjectUseAsset(projectRequestDTO.getProjectUseAsset());
            newProject.setProjectPHLCount(projectRequestDTO.getProjectPHLCount());
            newProject.setProjectUseResource(null);
            newProject.setProjectStartDate(projectRequestDTO.getProjectStartDate());
            newProject.setProjectEndDate(projectRequestDTO.getProjectEndDate());
        } else {// Penjualan
            if (projectRequestDTO.getProjectUseResource() != null) {
                for (String resourceId : projectRequestDTO.getProjectUseResource()) {
                    if (!validateResource(resourceId)) {
                        throw new IllegalArgumentException("Pastikan ID Resource sudah terdaftar dalam sistem");
                    }
                }
            }
            id = "P" + String.format("%03d", projectNumber) + todayFormatted;
            newProject.setId(id);
            newProject.setProjectUseResource(projectRequestDTO.getProjectUseResource());
            newProject.setProjectUseAsset(null);
            newProject.setProjectDeliveryAddress(projectRequestDTO.getProjectDeliveryAddress());
            newProject.setProjectPickupAddress(null);
            newProject.setProjectPHLCount(null);
            newProject.setProjectStartDate(projectRequestDTO.getProjectStartDate());
            newProject.setProjectEndDate(projectRequestDTO.getProjectEndDate());
        }
        newProject.setCreatedDate(today);
        Project savedProject = projectRepository.save(newProject);

        return projectToProjectResponseDetailDTO(savedProject);
    }

}
