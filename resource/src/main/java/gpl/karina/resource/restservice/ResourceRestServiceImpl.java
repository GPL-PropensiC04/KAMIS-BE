package gpl.karina.resource.restservice;


import gpl.karina.resource.exception.UserNotFound;
import gpl.karina.resource.exception.UserUnauthorized;
import gpl.karina.resource.model.Resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gpl.karina.resource.repository.ResourceRepository;
import gpl.karina.resource.restdto.request.AddResourceDTO;
import gpl.karina.resource.restdto.response.AddResourceResponseDTO;
import gpl.karina.resource.restdto.response.ListResourceResponseDTO;

@Service
public class ResourceRestServiceImpl implements ResourceRestService {
    private final ResourceRepository resourceRepository;

    public ResourceRestServiceImpl(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    private AddResourceResponseDTO resourceToResourceResponseDTO(Resource resource) {
        AddResourceResponseDTO addResourceResponseDTO = new AddResourceResponseDTO();
        addResourceResponseDTO.setResourceName(resource.getResourceName());
        addResourceResponseDTO.setResourceDescription(resource.getResourceDescription());   
        addResourceResponseDTO.setResourceSupplier(resource.getResourceSupplier());
        addResourceResponseDTO.setResourceStock(resource.getResourceStock());
        addResourceResponseDTO.setResourcePrice(resource.getResourcePrice());
        return addResourceResponseDTO;
    }
    @Override
    public AddResourceResponseDTO addResource(AddResourceDTO addResourceDTO) {
        if (addResourceDTO.getResourcePrice() < 0) {
            throw new IllegalArgumentException("Harga barang tidak boleh kurang dari 0");
        }
        if (addResourceDTO.getResourceStock() < 0) {
            throw new IllegalArgumentException("Stok barang tidak boleh kurang dari 0");
        }
        if (addResourceDTO.getResourceName() == null) {
            throw new IllegalArgumentException("Nama barang tidak boleh kosong");
        }
        if (addResourceDTO.getResourceDescription() == null) {
            throw new IllegalArgumentException("Deskripsi barang tidak boleh kosong");
        }
        Resource resource = new Resource();
        if (resourceRepository.findByResourceName(addResourceDTO.getResourceName()) != null) {
            throw new IllegalArgumentException("Nama barang sudah ada di database");
        }   
        resource.setResourceName(addResourceDTO.getResourceName());    
        resource.setResourceDescription(addResourceDTO.getResourceDescription());
        resource.setResourceSupplier(addResourceDTO.getResourceSupplier());
        resource.setResourceStock(addResourceDTO.getResourceStock());
        resource.setResourcePrice(addResourceDTO.getResourcePrice());
        
        resourceRepository.save(resource);
        return resourceToResourceResponseDTO(resource);
    }

    @Override
    public List<AddResourceResponseDTO> getAllResources() {
        // // Verifikasi token dan dapatkan informasi pengguna
        // EndUserResponseDTO currentUser = profileService.getCurrentUser(token);
        // if (currentUser == null) {
        //     throw new UserNotFound("You Must Login First");
        // }
    
        // // Periksa apakah pengguna memiliki peran ADMIN atau NURSE
        // String role = currentUser.getRole();
        // if (!"ADMIN".equalsIgnoreCase(role) && !"NURSE".equalsIgnoreCase(role)) {
        //     throw new UserUnauthorized("You are not authorized to view appointments");
        // }
    
        // Mengambil semua janji dari database
        List<Resource> resources = resourceRepository.findAll();
        List<AddResourceResponseDTO> responseDTOs = new ArrayList<>();
        resources.forEach(resource -> responseDTOs.add(resourceToResourceResponseDTO(resource)));
        return responseDTOs;
    }
}
