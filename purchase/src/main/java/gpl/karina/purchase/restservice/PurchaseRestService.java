package gpl.karina.purchase.restservice;

import gpl.karina.purchase.restdto.request.AddPurchaseDTO;
import gpl.karina.purchase.restdto.response.AddPurchaseResponseDTO;

public interface PurchaseRestService {
    AddPurchaseResponseDTO addPurchase(AddPurchaseDTO addPurchaseDTO);
}
