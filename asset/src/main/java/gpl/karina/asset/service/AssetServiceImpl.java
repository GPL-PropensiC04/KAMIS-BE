package gpl.karina.asset.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.asset.dto.response.AssetResponseDTO;
import gpl.karina.asset.dto.request.AssetUpdateRequestDTO;
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
    public AssetResponseDTO getAssetById(String platNomor) throws Exception {
        Asset asset = assetDb.findByIdAndNotDeleted(platNomor);
        
        if (asset != null) {
            return assetToAssetResponseDTO(asset);
        } else {
            throw new Exception("Asset tidak ditemukan");
        }
    }

    @Override
    @Transactional
    public void deleteAsset(String platNomor) throws Exception {
        Optional<Asset> optionalAsset = assetDb.findById(platNomor);
        
        if (optionalAsset.isPresent()) {
            assetDb.softDeleteById(platNomor);
        } else {
            throw new Exception("Asset dengan plat nomor " + platNomor + " tidak ditemukan");
        }
    }


    @Override
    public AssetResponseDTO updateAssetImage(String platNomor, byte[] imageData) throws Exception {
        Optional<Asset> optionalAsset = assetDb.findById(platNomor);
        
        if (optionalAsset.isPresent()) {
            Asset asset = optionalAsset.get();
            asset.setGambarAset(imageData);
            assetDb.save(asset);
            return assetToAssetResponseDTO(asset);
        } else {
            throw new Exception("Asset tidak ditemukan");
        }
    }

    @Override
    @Transactional
    public AssetResponseDTO updateAssetDetails(String platNomor, AssetUpdateRequestDTO updateRequest) throws Exception {
        Optional<Asset> optionalAsset = assetDb.findById(platNomor);
        
        if (optionalAsset.isPresent()) {
            Asset asset = optionalAsset.get();
            
            // Update fields if provided in the request
            if (updateRequest.getNama() != null) {
                asset.setNama(updateRequest.getNama());
            }
            
            if (updateRequest.getJenisAset() != null) {
                asset.setJenisAset(updateRequest.getJenisAset());
            }
            
            if (updateRequest.getStatus() != null) {
                asset.setStatus(updateRequest.getStatus());
            }
            
            if (updateRequest.getDeskripsi() != null) {
                asset.setDeskripsi(updateRequest.getDeskripsi());
            }
            
            if (updateRequest.getAssetMaintenance() != null) {
                asset.setAssetMaintenance(updateRequest.getAssetMaintenance());
            }
            
            Asset updatedAsset = assetDb.save(asset);
            return assetToAssetResponseDTO(updatedAsset);
        } else {
            throw new Exception("Asset dengan plat nomor " + platNomor + " tidak ditemukan");
        }
    }

    private AssetResponseDTO assetToAssetResponseDTO(Asset asset) {
        var assetResponseDTO = new AssetResponseDTO();
        assetResponseDTO.setPlatNomor(asset.getPlatNomor());
        assetResponseDTO.setNama(asset.getNama());
        assetResponseDTO.setJenisAset(asset.getJenisAset());
        assetResponseDTO.setStatus(asset.getStatus());
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