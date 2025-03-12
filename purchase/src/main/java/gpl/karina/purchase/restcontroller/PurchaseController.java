package gpl.karina.purchase.restcontroller;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpl.karina.purchase.exception.DataNotFound;
import gpl.karina.purchase.exception.UserUnauthorized;
import gpl.karina.purchase.restdto.request.AddPurchaseDTO;
import gpl.karina.purchase.restdto.request.AssetTempDTO;
import gpl.karina.purchase.restdto.request.UpdatePurchaseDTO;
import gpl.karina.purchase.restdto.response.AssetTempResponseDTO;
import gpl.karina.purchase.restdto.response.BaseResponseDTO;
import gpl.karina.purchase.restdto.response.PurchaseResponseDTO;
import gpl.karina.purchase.restservice.PurchaseRestService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/purchase")
public class PurchaseController {
    private final PurchaseRestService purchaseRestService;

    public PurchaseController(PurchaseRestService purchaseRestService) {
        this.purchaseRestService = purchaseRestService;
    }

    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO<PurchaseResponseDTO>> addPurchase(
            @Valid @RequestBody AddPurchaseDTO addPurchaseDTO, BindingResult bindingResult) {
        BaseResponseDTO<PurchaseResponseDTO> response = new BaseResponseDTO<>();
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages.toString());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            response.setStatus(200);
            response.setMessage("Success");
            response.setData(purchaseRestService.addPurchase(addPurchaseDTO));
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/viewall")
    public ResponseEntity<BaseResponseDTO<List<PurchaseResponseDTO>>> getAllPurchase(
            @RequestParam(required = false) Integer startNominal,
            @RequestParam(required = false) Integer endNominal,
            @RequestParam(required = false) Boolean highNominal,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
            @RequestParam(required = false) Boolean newDate,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false) String idSearch) {
        
        var baseResponseDTO = new BaseResponseDTO<List<PurchaseResponseDTO>>();
        
        try {
            List<PurchaseResponseDTO> purchases = purchaseRestService.getAllPurchase(
                    startNominal, endNominal, highNominal, startDate, endDate, newDate, type, idSearch);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("OK");
            baseResponseDTO.setData(purchases);
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{purchaseId}")
    public ResponseEntity<BaseResponseDTO<PurchaseResponseDTO>> updatePurchase(@PathVariable String purchaseId,
            @RequestBody UpdatePurchaseDTO updatePurchaseDTO) {
        var baseResponseDTO = new BaseResponseDTO<PurchaseResponseDTO>();
        // String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        
        try {
            PurchaseResponseDTO updatedPurchase = purchaseRestService.updatePurchase(updatePurchaseDTO, purchaseId);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("OK");
            baseResponseDTO.setData(updatedPurchase);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (UserUnauthorized e) {
            baseResponseDTO.setStatus(HttpStatus.FORBIDDEN.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.FORBIDDEN);
        } catch (DataNotFound e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setData(null);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addAsset")
    public ResponseEntity<BaseResponseDTO<AssetTempResponseDTO>> addAsset(
            @Valid @RequestBody AssetTempDTO assetTempDTO, BindingResult bindingResult) {
        BaseResponseDTO<AssetTempResponseDTO> response = new BaseResponseDTO<>();
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages.toString());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            response.setStatus(200);
            response.setMessage("Success");
            response.setData(purchaseRestService.addAsset(assetTempDTO));
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
