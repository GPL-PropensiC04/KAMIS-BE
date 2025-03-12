package gpl.karina.asset.service;

import java.util.List;
import gpl.karina.asset.dto.response.AssetResponseDTO;

public interface AssetService {
    List<AssetResponseDTO> getAllAsset();
    AssetResponseDTO getAssetById(String id) throws Exception;
    AssetResponseDTO updateAssetImage(String id, byte[] imageData) throws Exception;
}
