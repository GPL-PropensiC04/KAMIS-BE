package gpl.karina.resource.restservice;


import gpl.karina.resource.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import gpl.karina.resource.repository.ResourceRepository;
import gpl.karina.resource.restdto.request.AddResourceDTO;
import gpl.karina.resource.restdto.request.UpdateResourceDTO;
import gpl.karina.resource.restdto.response.ResourceResponseDTO;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ResourceRestServiceImpl implements ResourceRestService {
    private final ResourceRepository resourceRepository;

    public ResourceRestServiceImpl(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    private ResourceResponseDTO resourceToResourceResponseDTO(Resource resource) {
        ResourceResponseDTO addResourceResponseDTO = new ResourceResponseDTO();
        addResourceResponseDTO.setId(resource.getId());
        addResourceResponseDTO.setResourceName(resource.getResourceName());
        addResourceResponseDTO.setResourceDescription(resource.getResourceDescription());   
        addResourceResponseDTO.setResourceStock(resource.getResourceStock());
        addResourceResponseDTO.setResourcePrice(resource.getResourcePrice());
        // addResourceResponseDTO.setResourceSupplierId(resource.getSupplierId());
        return addResourceResponseDTO;
    }

    @Override
    public ResourceResponseDTO addResource(AddResourceDTO addResourceDTO) {
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
        if (addResourceDTO.getResourceSupplierId() == null) {
            throw new IllegalArgumentException("Supplier ID tidak boleh kosong");
        }
        Resource resource = new Resource();
        if (resourceRepository.findByResourceName(addResourceDTO.getResourceName()) != null) {
            throw new IllegalArgumentException("Nama barang sudah ada di database");
        }   
        resource.setResourceName(addResourceDTO.getResourceName());    
        resource.setResourceDescription(addResourceDTO.getResourceDescription());
        resource.setResourceStock(addResourceDTO.getResourceStock());
        resource.setResourcePrice(addResourceDTO.getResourcePrice());
        List<UUID> supplierIds = new ArrayList<>();
        supplierIds.add(UUID.fromString(addResourceDTO.getResourceSupplierId()));
        resource.setSupplierId(supplierIds);
        
        resourceRepository.save(resource);
        return resourceToResourceResponseDTO(resource);
    }

    @Override
    public List<ResourceResponseDTO> getAllResources() {
        List<Resource> resources = resourceRepository.findAll();
        List<ResourceResponseDTO> responseDTOs = new ArrayList<>();
        resources.forEach(resource -> responseDTOs.add(resourceToResourceResponseDTO(resource)));
        return responseDTOs;
    }

    @Override
    public ResourceResponseDTO updateResource(UpdateResourceDTO updateResourceDTO, Long idResource) {

        Resource resource = resourceRepository.findById(idResource).orElseThrow(() -> new IllegalArgumentException("Resource not found"));
        if (updateResourceDTO.getResourcePrice() < 0) {
            throw new IllegalArgumentException("Harga barang tidak boleh kurang dari 0");
        }
        if (updateResourceDTO.getResourceDescription() == null) {
            throw new IllegalArgumentException("Deskripsi barang tidak boleh kosong");
        }     
        resource.setResourceDescription(updateResourceDTO.getResourceDescription());
        resource.setResourcePrice(updateResourceDTO.getResourcePrice());
        resource.setResourceStock(resource.getResourceStock() + updateResourceDTO.getResourceStock());
        
        resourceRepository.save(resource);
        return resourceToResourceResponseDTO(resource);
    }

    /**
     * Adds stock to a resource
     * @param idResource The resource ID
     * @param quantity The quantity to add (must be positive)
     * @return Updated resource response
     */
    @Override
    public ResourceResponseDTO addResourceStock(Long idResource, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah penambahan stok harus positif");
        }
        
        Resource resource = resourceRepository.findByIdWithPessimisticLock(idResource)
            .orElseThrow(() -> new IllegalArgumentException("Resource tidak ditermukan"));
        
        // Add the stock
        resource.setResourceStock(resource.getResourceStock() + quantity);
        
        // Save and return
        resourceRepository.save(resource);
        return resourceToResourceResponseDTO(resource);
    }

    /**
     * Deducts stock from a resource
     * @param idResource The resource ID
     * @param quantity The quantity to deduct (must be positive)
     * @return Updated resource response
     * @throws IllegalArgumentException if insufficient stock
     */
    @Override
    public ResourceResponseDTO deductResourceStock(Long idResource, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah pengurangan stok harus positif");
        }
        
        Resource resource = resourceRepository.findByIdWithPessimisticLock(idResource)
            .orElseThrow(() -> new IllegalArgumentException("Resource tidak ditermukan"));
        
        // Resource resource = resourceRepository.findById(idResource)
        //     .orElseThrow(() -> new IllegalArgumentException("Resource tidak ditemukan"));

        // Check if we have enough stock
        int newStock = resource.getResourceStock() - quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException(
                "Stock tidak mencukupi. Tersedia: " + resource.getResourceStock() + 
                ", permintaan: " + quantity);
        }
        
        // Update the stock
        resource.setResourceStock(newStock);
        
        // Save and return
        resourceRepository.save(resource);
        return resourceToResourceResponseDTO(resource);
    }

    @Override
    public ResourceResponseDTO getResourceById(Long idResource) {
        Resource resource = resourceRepository.findById(idResource).orElseThrow(() -> new IllegalArgumentException("Resource not found"));
        return resourceToResourceResponseDTO(resource);
    }

    @Override
    public ResourceResponseDTO addResourceToDbById(Long idResource, Integer stock) {

        Resource resource = resourceRepository.findById(idResource).orElseThrow(() -> new IllegalArgumentException("Resource not found"));
        resource.setResourceStock(resource.getResourceStock() + stock);
        
        resourceRepository.save(resource);
        return resourceToResourceResponseDTO(resource);
    }

    @Override
    public List<ResourceResponseDTO> getAllSuplierResosource(UUID idSupplier) {
        List<Resource> resources = resourceRepository.findBySupplierId(idSupplier);
        List<ResourceResponseDTO> responseDTOs = new ArrayList<>();
        resources.forEach(resource -> responseDTOs.add(resourceToResourceResponseDTO(resource)));
        return responseDTOs;
    }
}
