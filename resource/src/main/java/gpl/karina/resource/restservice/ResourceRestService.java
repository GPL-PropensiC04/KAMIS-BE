package gpl.karina.resource.restservice;

import java.util.List;

import gpl.karina.resource.restdto.request.AddResourceDTO;
import gpl.karina.resource.restdto.response.ResourceResponseDTO;

public interface ResourceRestService {
    ResourceResponseDTO addResource(AddResourceDTO addResourceDTO);
    List<ResourceResponseDTO> getAllResources();
}