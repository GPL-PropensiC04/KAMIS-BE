package gpl.karina.asset.controller;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}