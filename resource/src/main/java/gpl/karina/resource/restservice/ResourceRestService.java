package gpl.karina.resource.restservice;

import java.util.List;

import gpl.karina.resource.restdto.request.AddResourceDTO;
import gpl.karina.resource.restdto.request.UpdateResourceDTO;
import gpl.karina.resource.restdto.response.ResourceResponseDTO;

public interface ResourceRestService {
    ResourceResponseDTO addResource(AddResourceDTO addResourceDTO);
    List<ResourceResponseDTO> getAllResources();
    ResourceResponseDTO updateResource(UpdateResourceDTO updateResourceDTO, Long idResource);
    ResourceResponseDTO getResourceById(Long idResource);
    ResourceResponseDTO addResourceToDbById(Long idResource, Integer stock);
    ResourceResponseDTO addResourceStock(Long idResource, Integer quantity);
    ResourceResponseDTO deductResourceStock(Long idResource, Integer quantity);
}