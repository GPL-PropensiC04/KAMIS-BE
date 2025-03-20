package gpl.karina.asset.controller;

import java.util.Date;
import java.util.List;
import java.util.Base64;

import org.springframework.http.MediaType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import gpl.karina.asset.model.Asset;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.dto.request.AssetUpdateRequestDTO;
import gpl.karina.asset.dto.request.AssetAddDTO;
import gpl.karina.asset.dto.response.AssetResponseDTO;
import gpl.karina.asset.dto.response.BaseResponseDTO;
import gpl.karina.asset.service.AssetService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/asset")
public class AssetController {
    private final AssetService assetService;
    private final AssetDb assetDb;

    public AssetController(AssetService assetService, AssetDb assetDb) {
        this.assetService = assetService;
        this.assetDb = assetDb;
    }

    @GetMapping("/all")
    public ResponseEntity<?> listAsset() {
        var baseResponseDTO = new BaseResponseDTO<List<AssetResponseDTO>>();
        List<AssetResponseDTO> listAsset = assetService.getAllAsset();

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(listAsset);
        baseResponseDTO.setMessage(String.format("List Asset berhasil ditemukan"));
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }
    
    @GetMapping("/{platNomor}")
    public ResponseEntity<?> getAssetDetail(@PathVariable("platNomor") String platNomor) {
        var baseResponseDTO = new BaseResponseDTO<AssetResponseDTO>();
        
        try {
            AssetResponseDTO asset = assetService.getAssetById(platNomor);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(asset);
            baseResponseDTO.setMessage("Detail Asset berhasil ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Asset dengan plat nomor " + platNomor + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{platNomor}")
    public ResponseEntity<?> deleteAsset(@PathVariable("platNomor") String platNomor) {
        var baseResponseDTO = new BaseResponseDTO<Void>();
        
        try {
            assetService.deleteAsset(platNomor);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Asset berhasil dihapus");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{platNomor}")
    public ResponseEntity<?> updateAsset(@PathVariable("platNomor") String platNomor, 
                                       @RequestBody AssetUpdateRequestDTO updateRequest) {
        var baseResponseDTO = new BaseResponseDTO<AssetResponseDTO>();
        
        try {
            AssetResponseDTO updatedAsset = assetService.updateAssetDetails(platNomor, updateRequest);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(updatedAsset);
            baseResponseDTO.setMessage("Detail Asset berhasil diperbarui");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/addAsset", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponseDTO<AssetResponseDTO>> addAsset(
            @Valid @ModelAttribute AssetAddDTO assetTempDTO, BindingResult bindingResult) {
        BaseResponseDTO<AssetResponseDTO> response = new BaseResponseDTO<>();
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages.toString());
            response.setTimestamp(new Date());
            return new ResponseEntity<BaseResponseDTO<AssetResponseDTO>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            response.setStatus(200);
            response.setMessage("Success");
            response.setData(assetService.addAsset(assetTempDTO));
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            System.out.println(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<?> getAssetFoto(@PathVariable String id) {
        try {
            Asset asset = assetService.getAssetFoto(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(asset.getFotoContentType()))
                    .body(asset.getFoto());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}