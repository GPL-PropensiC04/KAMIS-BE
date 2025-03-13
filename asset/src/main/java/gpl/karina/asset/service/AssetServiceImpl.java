package gpl.karina.asset.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.asset.dto.response.AssetResponseDTO;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.model.Asset;

import java.util.ArrayList;
import java.util.Base64;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    // private JwtTokenHolder tokenHolder;

    private final AssetDb assetDb;

    public AssetServiceImpl(AssetDb assetDb, WebClient.Builder webClientBuilder) {
        this.assetDb = assetDb;
    }

    @Override
    public List<AssetResponseDTO> getAllAsset() {

        var listAsset = assetDb.findAllActive();
        var listAssetResponseDTO = new ArrayList<AssetResponseDTO>();
        listAsset.forEach(asset -> {
            var assetResponseDTO = assetToAssetResponseDTO(asset);
            listAssetResponseDTO.add(assetResponseDTO);
        });
        return listAssetResponseDTO;
    }
    
    @Override
    public AssetResponseDTO getAssetById(String id) throws Exception {

        Asset asset = assetDb.findByIdAndNotDeleted(id);
        
        if (asset != null) {
            return assetToAssetResponseDTO(asset);
        } else {
            throw new Exception("Asset tidak ditemukan");
        }
    }

    @Override
    @Transactional
    public void deleteAsset(String id) throws Exception {
        Optional<Asset> optionalAsset = assetDb.findById(id);
        
        if (optionalAsset.isPresent()) {
            assetDb.softDeleteById(id);
        } else {
            throw new Exception("Asset dengan ID " + id + " tidak ditemukan");
        }
    }

    @Override
    public AssetResponseDTO updateAssetImage(String id, byte[] imageData) throws Exception {
        Optional<Asset> optionalAsset = assetDb.findById(id);
        
        if (optionalAsset.isPresent()) {
            Asset asset = optionalAsset.get();
            asset.setGambarAset(imageData);
            assetDb.save(asset);
            return assetToAssetResponseDTO(asset);
        } else {
            throw new Exception("Asset tidak ditemukan");
        }
    }

    private AssetResponseDTO assetToAssetResponseDTO(Asset asset) {
        var assetResponseDTO = new AssetResponseDTO();
        assetResponseDTO.setId(asset.getId());
        assetResponseDTO.setNama(asset.getNama());
        assetResponseDTO.setDeskripsi(asset.getDeskripsi());
        assetResponseDTO.setTanggalPerolehan(asset.getTanggalPerolehan());
        assetResponseDTO.setNilaiPerolehan(asset.getNilaiPerolehan());
        assetResponseDTO.setAssetMaintenance(asset.getAssetMaintenance());
        
        // Konversi byte array gambar ke Base64 string untuk dikirim ke frontend
        if (asset.getGambarAset() != null && asset.getGambarAset().length > 0) {
            String base64Image = Base64.getEncoder().encodeToString(asset.getGambarAset());
            assetResponseDTO.setGambarAsetBase64(base64Image);
        }
        
        return assetResponseDTO;
    }
}