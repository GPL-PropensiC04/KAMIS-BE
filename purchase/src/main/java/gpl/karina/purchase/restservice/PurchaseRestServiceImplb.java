package gpl.karina.purchase.restservice;

import java.util.Date;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import gpl.karina.purchase.model.AssetTemp;
import gpl.karina.purchase.model.Purchase;
import gpl.karina.purchase.model.ResourceTemp;
import gpl.karina.purchase.repository.AssetTempRepository;
import gpl.karina.purchase.repository.PurchaseRepository;
import gpl.karina.purchase.repository.ResourceTempRepository;
import gpl.karina.purchase.restdto.request.AddPurchaseDTO;
import gpl.karina.purchase.restdto.request.ResourceTempDTO;
import gpl.karina.purchase.restdto.response.AssetTempResponseDTO;
import gpl.karina.purchase.restdto.response.PurchaseResponseDTO;
import gpl.karina.purchase.restdto.response.ResourceTempResponseDTO;

@Service
public class PurchaseRestServiceImplb implements PurchaseRestService {
    private final PurchaseRepository purchaseRepository;
    private final AssetTempRepository assetTempRepository;
    private final ResourceTempRepository resourceTempRepository;

    public PurchaseRestServiceImplb(PurchaseRepository purchaseRepository, AssetTempRepository assetTempRepository, ResourceTempRepository resourceTempRepository) {
        this.purchaseRepository = purchaseRepository;
        this.assetTempRepository = assetTempRepository;
        this.resourceTempRepository = resourceTempRepository;
    }

    private ResourceTempResponseDTO resourceTempToResourceTempResponseDTO (ResourceTemp resourceTemp) {
        ResourceTempResponseDTO resourceTempResponseDTO = new ResourceTempResponseDTO();
        resourceTempResponseDTO.setResourceId(resourceTemp.getResourceId());
        resourceTempResponseDTO.setResourceName(resourceTemp.getResourceName());
        resourceTempResponseDTO.setResourceTotal(resourceTemp.getResourceTotal());
        resourceTempResponseDTO.setResourcePrice(resourceTemp.getResourcePrice());
        return resourceTempResponseDTO;
    }

    private AssetTempResponseDTO assetTempToAssetTempResponseDTO (AssetTemp assetTemp) {
        AssetTempResponseDTO assetTempResponseDTO = new AssetTempResponseDTO();
        assetTempResponseDTO.setAssetNameString(assetTemp.getAssetName());
        assetTempResponseDTO.setAssetDescription(assetTemp.getAssetDescription());
        assetTempResponseDTO.setAssetType(assetTemp.getAssetType());
        assetTempResponseDTO.setAssetPrice(assetTemp.getAssetPrice());
        return assetTempResponseDTO;
    }

    private PurchaseResponseDTO purchaseToPurchaseResponseDTO(Purchase purchase) {
        PurchaseResponseDTO purchaseResponseDTO = new PurchaseResponseDTO();
        purchaseResponseDTO.setPurchaseId(purchase.getId());
        purchaseResponseDTO.setPurchaseSubmissionDate(purchase.getPurchaseSubmissionDate());
        purchaseResponseDTO.setPurchaseUpdateDate(purchase.getPurchaseUpdateDate());
        purchaseResponseDTO.setPurchaseSupplier(purchase.getPurchaseSupplier());
        purchaseResponseDTO.setPurchaseNote(purchase.getPurchaseNote());

        Boolean purchaseType = purchase.isPurchaseType();
        if (purchaseType) {
            purchaseResponseDTO.setPurchaseType("Resource");
            
            List<ResourceTemp> resourceTemps = purchase.getPurchaseResource();
            List<ResourceTempResponseDTO> resourceTempResponseDTOs = new ArrayList<>();
            for (ResourceTemp resourceTemp: resourceTemps) {
                resourceTempResponseDTOs.add(resourceTempToResourceTempResponseDTO(resourceTemp));
            }
            purchaseResponseDTO.setPurchaseResource(resourceTempResponseDTOs);
        } else {
            purchaseResponseDTO.setPurchaseType("Aset");

            AssetTemp assetTemp = assetTempRepository.findById(purchase.getPurchaseAsset()).orElse(null);
            purchaseResponseDTO.setPurchaseAsset(assetTempToAssetTempResponseDTO(assetTemp));
        }
        
        purchaseResponseDTO.setPurchaseStatus(purchase.getPurchaseStatus());

        return purchaseResponseDTO;
    }

    @Override
    public PurchaseResponseDTO addPurchase(AddPurchaseDTO addPurchaseDTO) {
        if (addPurchaseDTO.getPurchaseSupplier() == null) {
            throw new IllegalArgumentException("Supplier tidak boleh kosong");
        }

        Purchase purchase = new Purchase();
        purchase.setPurchaseSupplier(addPurchaseDTO.getPurchaseSupplier());
        purchase.setPurchaseType(addPurchaseDTO.isPurchaseType());
        purchase.setPurchaseStatus("Diajukan");
        purchase.setPurchaseNote(addPurchaseDTO.getPurchaseNote());

        String id = "";
        if (purchase.isPurchaseType()) {
            id += "R-";

            List<ResourceTempDTO> resourceDTO = addPurchaseDTO.getPurchaseResource();
            if (resourceDTO == null || resourceDTO.isEmpty()) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian resource, pastikan menginput data resource setidaknya satu.");
            }
            if (addPurchaseDTO.getPurchaseAsset() != null) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian resource, pastikan tidak menginput data aset.");
            }

            List<ResourceTemp> resourceTemps = new ArrayList<>();
            Integer purchasePrice = 0;

            Set<Long> existingIds = new HashSet<>();
            for (ResourceTempDTO resourceInput : resourceDTO) {
                if (resourceInput.getResourceId() != null && !existingIds.add(resourceInput.getResourceId())) {
                    throw new IllegalArgumentException("Tidak boleh terdapat lebih dari satu resource yang sama!");
                }
                ResourceTemp resourceTemp = new ResourceTemp();
                resourceTemp.setResourceId(resourceInput.getResourceId());
                resourceTemp.setResourceName(resourceInput.getResourceName());
                resourceTemp.setResourceTotal(resourceInput.getResourceTotal());
                resourceTemp.setResourcePrice(resourceInput.getResourcePrice());

                purchasePrice += resourceTemp.getResourcePrice();
                resourceTempRepository.save(resourceTemp);
                resourceTemps.add(resourceTemp);
            }
            purchase.setPurchaseResource(resourceTemps);
            purchase.setPurchasePrice(purchasePrice);
        } else {
            id += "A-";

            if (addPurchaseDTO.getPurchaseAsset() == null) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian aset, pastikan menginput data aset.");
            }
            if (addPurchaseDTO.getPurchaseResource() != null) {
                throw new IllegalArgumentException("Anda memilih tipe pembelian aset, pastikan tidak menginput data resource.");
            }

            AssetTemp assetTemp = assetTempRepository.findById(addPurchaseDTO.getPurchaseAsset()).orElse(null);

            purchase.setPurchaseAsset(assetTemp.getId());
            purchase.setPurchasePrice(assetTemp.getAssetPrice());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        Date today = new Date(); // Get current date

        id += sdf.format(today) + "-";

        // Fetch all purchases and filter for today
        List<Purchase> allPurchases = purchaseRepository.findAll();
        long purchasesCountToday = allPurchases.stream()
            .filter(p -> isSameDay(p.getPurchaseSubmissionDate(), today)) // Renamed `purchase` to `p`
            .count();

        long newIdNumber = purchasesCountToday + 1;

        id += String.format("%03d", newIdNumber);
        purchase.setId(id);

        Purchase newPurchase = purchaseRepository.save(purchase);

        return purchaseToPurchaseResponseDTO(newPurchase);
    }
    
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

}
