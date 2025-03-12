package gpl.karina.purchase.restservice;

import gpl.karina.purchase.restdto.request.AddPurchaseDTO;
import gpl.karina.purchase.restdto.response.PurchaseResponseDTO;

public interface PurchaseRestService {
    PurchaseResponseDTO addPurchase(AddPurchaseDTO addPurchaseDTO);
}
