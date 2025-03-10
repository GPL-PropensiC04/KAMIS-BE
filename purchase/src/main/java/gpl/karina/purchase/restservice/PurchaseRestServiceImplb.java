package gpl.karina.purchase.restservice;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;

import gpl.karina.purchase.model.AssetTemp;
import gpl.karina.purchase.model.Purchase;
import gpl.karina.purchase.repository.AssetTempRepository;
import gpl.karina.purchase.repository.PurchaseRepository;
import gpl.karina.purchase.repository.ResourceTempRepository;
import gpl.karina.purchase.restdto.request.AddPurchaseDTO;
import gpl.karina.purchase.restdto.request.ResourceTempDTO;
import gpl.karina.purchase.restdto.response.AddPurchaseResponseDTO;

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

    @Override
    public AddPurchaseResponseDTO addPurchase(AddPurchaseDTO addPurchaseDTO) {
        if (addPurchaseDTO.getPurchaseSupplier() == null) {
            throw new IllegalArgumentException("Supplier tidak boleh kosong");
        }

        Purchase purchase = new Purchase();
        purchase.setPurchaseSupplier(addPurchaseDTO.getPurchaseSupplier());
        purchase.setPurchaseType(addPurchaseDTO.isPurchaseType());
        purchase.setPurchaseStatus("Diajukan");

        String id = "";
        if (purchase.isPurchaseType()) {
            id += "R-";

            List<ResourceTempDTO> resourceDTO = addPurchaseDTO.getPurchaseResource();
            //TODO: lanjutin bikin loop buat simpan resource nya
        } else {
            id += "A-";

            AssetTemp aset = assetTempRepository.findById(addPurchaseDTO.getPurchaseAsset()).orElse(null);
            purchase.setPurchasePrice(aset.getAssetPrice());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        LocalDate today = LocalDate.now();

        id += sdf.format(today) + "-";

        long purchasesCountToday = purchaseRepository.countByPurchaseSubmissionDate(today);
        long newIdNumber = purchasesCountToday + 1;

        id += String.format("%03d", newIdNumber);

        //TODO: lanjutin simpan purchase dan convert ke respponse dto
    }
}
