package gpl.karina.project.restservice;

import gpl.karina.project.restdto.request.ProjectRequestDTO;
import gpl.karina.project.restdto.response.ProjectResponseDTO;

public interface ProjectService {
    public ProjectResponseDTO addProject(ProjectRequestDTO projectRequestDTO) throws Exception;
    // public List<ProjectResponseDTO> getAllProject() throws Exception;
    // public ProjectResponseDTO getProjectById(String id) throws Exception;
}
