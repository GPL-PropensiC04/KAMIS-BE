package gpl.karina.project.restservice;

import gpl.karina.project.restdto.request.AddProjectRequestDTO;
import gpl.karina.project.restdto.request.UpdateProjectRequestDTO;
import gpl.karina.project.restdto.response.ActivityLineDTO;
import gpl.karina.project.restdto.response.ProjectResponseWrapperDTO;
import gpl.karina.project.restdto.response.SellDistributionSummaryDTO;
import gpl.karina.project.restdto.response.listProjectResponseDTO;
import java.util.List;
import java.util.Date;

public interface ProjectService {
    public ProjectResponseWrapperDTO addProject(AddProjectRequestDTO projectRequestDTO) throws Exception;

    public ProjectResponseWrapperDTO updateProject(UpdateProjectRequestDTO UpdateProjectRequestDTO) throws Exception;

    public List<listProjectResponseDTO> getAllProject(
            String idSearch, String projectStatus, String projectType,
            String projectName, String projectClientId, Date projectStartDate,
            Date projectEndDate, Long startNominal, Long endNominal) throws Exception;

    public ProjectResponseWrapperDTO getProjectById(String id) throws Exception;

    public ProjectResponseWrapperDTO updateProjectStatus(String id, Integer projectStatus) throws Exception;

    public ProjectResponseWrapperDTO updateProjectPayment(String id, Integer projectPaymentStatus) throws Exception;
    // public ProjectResponseDTO getProjectById(String id) throws Exception;

    List<ActivityLineDTO> getProjectActivityLine(String periodType, String range, String statusFilter, boolean isDistribusi);
    SellDistributionSummaryDTO getSellDistributionSummaryByRange(String range);
    List<listProjectResponseDTO> getProjectListByRange(String range);
}
