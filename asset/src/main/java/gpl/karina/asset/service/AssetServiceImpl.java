package gpl.karina.asset.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.asset.dto.response.AssetResponseDTO;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.model.Asset;

import java.util.ArrayList;

public class AssetServiceImpl implements AssetService {

    @Autowired
    // private JwtTokenHolder tokenHolder;

    private final AssetDb assetDb;

    public AssetServiceImpl(AssetDb assetDb, WebClient.Builder webClientBuilder) {
        this.assetDb = assetDb;
    }

    @Override
    public List<AssetResponseDTO> getAllAsset() {
        var listAsset = assetDb.findAll();
        var listAssetResponseDTO = new ArrayList<AssetResponseDTO>();
        listAsset.forEach(asset -> {
            var assetResponseDTO = assetToAssetResponseDTO(asset);
            listAssetResponseDTO.add(assetResponseDTO);
        });
        return listAssetResponseDTO;
    }

    private AssetResponseDTO assetToAssetResponseDTO(Asset asset) {
        var assetResponseDTO = new AssetResponseDTO();
        assetResponseDTO.setId(asset.getId());
        assetResponseDTO.setNama(asset.getNama());
        assetResponseDTO.setDeskripsi(asset.getDeskripsi());
        assetResponseDTO.setTanggalPerolehan(asset.getTanggalPerolehan());
        assetResponseDTO.setNilaiPerolehan(asset.getNilaiPerolehan());
        assetResponseDTO.setAssetMaintenance(asset.getAssetMaintenance());
        // assetResponseDTO.setHistoriMaintenance(asset.getHistoriMaintenance());
        return assetResponseDTO;
    }
}