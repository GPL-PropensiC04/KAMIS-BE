package gpl.karina.resource.restservice;

import gpl.karina.resource.restdto.request.AddResourceDTO;
import gpl.karina.resource.restdto.response.AddResourceResponseDTO;

public interface ResourceRestService {
    AddResourceResponseDTO addResource(AddResourceDTO addResourceDTO);
}
