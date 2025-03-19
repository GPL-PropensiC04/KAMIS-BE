package gpl.karina.asset.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Date;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.asset.dto.response.AssetResponseDTO;
import gpl.karina.asset.dto.request.AssetAddDTO;
import gpl.karina.asset.dto.request.AssetUpdateRequestDTO;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.model.Asset;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
            asset.setFoto(imageData);
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
        // assetResponseDTO.setAssetMaintenance(asset.getAssetMaintenance());
        assetResponseDTO.setFotoContentType(asset.getFotoContentType());
        assetResponseDTO.setFotoUrl("/api/asset/" + asset.getPlatNomor() + "/foto");
        
        return assetResponseDTO;
    }

    @Override
    public AssetResponseDTO addAsset(AssetAddDTO assetTempDTO) {
        if (assetTempDTO.getAssetName() == null) {
            throw new IllegalArgumentException("Nama Aset tidak boleh kosong");
        }
        if (assetTempDTO.getAssetDescription() == null) {
            throw new IllegalArgumentException("Deskripsi Aset tidak boleh kosong");
        }
        if (assetTempDTO.getAssetType() == null) {
            throw new IllegalArgumentException("Tipe Aset tidak boleh kosong");
        }
        if (assetTempDTO.getAssetPrice() == null) {
            throw new IllegalArgumentException("Harga Aset tidak boleh kosong");
        }
        if (assetTempDTO.getPlatNomor() == null) {
            throw new IllegalArgumentException("Plat Nomor tidak boleh kosong");
        }
        if (assetTempDTO.getStatus() == null) {
            throw new IllegalArgumentException("Status tidak boleh kosong");
        }

        Asset assetTemp = new Asset();
        assetTemp.setPlatNomor(assetTempDTO.getPlatNomor());
        assetTemp.setNama(assetTempDTO.getAssetName());
        assetTemp.setDeskripsi(assetTempDTO.getAssetDescription());
        assetTemp.setJenisAset(assetTempDTO.getAssetType());
        assetTemp.setNilaiPerolehan(assetTempDTO.getAssetPrice());
        assetTemp.setStatus(assetTempDTO.getStatus());

        // Convert string to Date
        if (assetTempDTO.getTanggalPerolehan() != null) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date tanggalPerolehan = formatter.parse(assetTempDTO.getTanggalPerolehan());
                assetTemp.setTanggalPerolehan(tanggalPerolehan);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Format tanggal tidak valid");
            }
        }

        if (assetTempDTO.getFoto() != null && !assetTempDTO.getFoto().isEmpty()) {
            try {
                assetTemp.setGambarAset(assetTempDTO.getFoto().getBytes());
                assetTemp.setFotoContentType(assetTempDTO.getFoto().getContentType());
            } catch (IOException e) {
                throw new IllegalArgumentException("Gagal mengupload foto");
            }
        }

        Asset newAssetTemp = assetDb.save(assetTemp);
        assetDb.save(assetTemp);
        return assetToAssetResponseDTO(newAssetTemp);
    }

    @Override
    public Asset getAssetFoto(String id) {
        return assetDb.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
    }
    
}