package gpl.karina.asset.service;

import java.util.List;
import java.util.UUID;

import gpl.karina.asset.dto.response.AssetListResponseDTO;
import gpl.karina.asset.dto.response.AssetResponseDTO;
import gpl.karina.asset.model.Asset;
import gpl.karina.asset.dto.request.AssetAddDTO;
import gpl.karina.asset.dto.request.AssetUpdateRequestDTO;

public interface AssetService {
    List<AssetListResponseDTO> getAllAsset();
    AssetResponseDTO getAssetById(String id) throws Exception;
    AssetResponseDTO updateAssetImage(String id, byte[] imageData) throws Exception;
    void deleteAsset(String id) throws Exception;
    AssetResponseDTO updateAssetDetails(String platNomor, AssetUpdateRequestDTO updateRequest) throws Exception;
    AssetResponseDTO addAsset(AssetAddDTO assetAddDTO);
    Asset getAssetFoto(String id);
    List<AssetResponseDTO> getAssetsBySupplier(UUID supplierId);

}
