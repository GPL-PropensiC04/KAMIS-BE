package gpl.karina.project.restservice;

import gpl.karina.project.restdto.request.ProjectRequestDTO;
import gpl.karina.project.restdto.response.ProjectResponseWrapperDTO;
import gpl.karina.project.restdto.response.listProjectResponseDTO;
import java.util.List;
import java.util.Date;
public interface ProjectService {
    public ProjectResponseWrapperDTO addProject(ProjectRequestDTO projectRequestDTO) throws Exception;
    public List<listProjectResponseDTO> getAllProject(
        String idSearch, String projectStatus, String projectType,
        String projectName, String projectClientId, Date projectStartDate,
        Date projectEndDate
    ) throws Exception;
    public ProjectResponseWrapperDTO updateProjectStatus(String id, Integer projectStatus) throws Exception;
    public ProjectResponseWrapperDTO updateProjectPayment(String id, boolean projectPaymentStatus) throws Exception;
    // public ProjectResponseDTO getProjectById(String id) throws Exception;
}
