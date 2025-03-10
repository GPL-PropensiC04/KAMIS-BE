package gpl.karina.resource.restservice;

import java.util.List;

import gpl.karina.resource.restdto.request.AddResourceDTO;
import gpl.karina.resource.restdto.response.AddResourceResponseDTO;
import gpl.karina.resource.restdto.response.ListResourceResponseDTO;

public interface ResourceRestService {
    AddResourceResponseDTO addResource(AddResourceDTO addResourceDTO);
    List<AddResourceResponseDTO> getAllResources();
}