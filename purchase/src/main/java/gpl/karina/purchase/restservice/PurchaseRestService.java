package gpl.karina.purchase.restservice;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import gpl.karina.purchase.restdto.request.AddPurchaseDTO;
import gpl.karina.purchase.restdto.request.AssetTempDTO;
import gpl.karina.purchase.restdto.request.UpdatePurchaseDTO;
import gpl.karina.purchase.restdto.request.UpdatePurchaseStatusDTO;
import gpl.karina.purchase.restdto.response.ActivityLineDTO;
import gpl.karina.purchase.restdto.response.AssetTempResponseDTO;
import gpl.karina.purchase.restdto.response.PurchaseListResponseDTO;
import gpl.karina.purchase.restdto.response.PurchaseResponseDTO;
import gpl.karina.purchase.restdto.response.PurchaseSummaryResponseDTO;

public interface PurchaseRestService {
    PurchaseResponseDTO addPurchase(AddPurchaseDTO addPurchaseDTO);
    List<PurchaseListResponseDTO> getAllPurchase(Integer startNominal, Integer endNominal,
        Boolean highNominal, Date startDate, Date endDate, Boolean newDate, String type, String idSearch);
    AssetTempResponseDTO addAsset(AssetTempDTO assetTempDTO);
    List<AssetTempResponseDTO> getAllAssets();
    PurchaseResponseDTO updatePurchase(UpdatePurchaseDTO updatePurchaseDTO, String purchaseId);
    PurchaseResponseDTO getDetailPurchase(String purchaseId);
    PurchaseResponseDTO updatePurchaseStatusToCancelled(UpdatePurchaseStatusDTO updatePurchaseStatusDTO, String purchaseId);
    PurchaseResponseDTO updatePurchaseStatusToNext(UpdatePurchaseStatusDTO updatePurchaseStatusDTO, String purchaseId);
    PurchaseResponseDTO updatePurchaseStatusPembayaran(UpdatePurchaseStatusDTO updatePurchaseStatusDTO, String purchaseId);
    AssetTempResponseDTO getDetailAsset(Long id);
    List<PurchaseResponseDTO> getPurchasesBySupplier(UUID supplierId);
    List<ActivityLineDTO> getPurchaseActivityLine(String periodType, String range, String statusFilter);
    List<PurchaseListResponseDTO> getPurchaseListByRange(String range);
    PurchaseSummaryResponseDTO getPurchaseSummaryByRange(String range);
}
