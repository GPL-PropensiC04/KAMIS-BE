package gpl.karina.asset.controller;

import java.util.Date;
import java.util.List;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import gpl.karina.asset.dto.response.AssetResponseDTO;
import gpl.karina.asset.dto.response.BaseResponseDTO;
import gpl.karina.asset.service.AssetService;

@RestController
@RequestMapping("/api/asset")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
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
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssetDetail(@PathVariable("id") String id) {
        var baseResponseDTO = new BaseResponseDTO<AssetResponseDTO>();
        
        try {
            AssetResponseDTO asset = assetService.getAssetById(id);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(asset);
            baseResponseDTO.setMessage("Detail Asset berhasil ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setData(null);
            baseResponseDTO.setMessage("Asset dengan ID " + id + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable("id") String id) {
        var baseResponseDTO = new BaseResponseDTO<Void>();
        
        try {
            assetService.deleteAsset(id);
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
}