package gpl.karina.asset.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Date;
import java.text.ParseException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import gpl.karina.asset.dto.response.AssetListResponseDTO;
import gpl.karina.asset.dto.response.AssetResponseDTO;
import gpl.karina.asset.dto.request.AssetAddDTO;
import gpl.karina.asset.dto.request.AssetUpdateRequestDTO;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.repository.MaintenanceRepository;
import gpl.karina.asset.model.Asset;
import gpl.karina.asset.model.Maintenance;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    // private JwtTokenHolder tokenHolder;

    private final AssetDb assetDb;
    private final MaintenanceRepository maintenanceRepository;

    public AssetServiceImpl(AssetDb assetDb, WebClient.Builder webClientBuilder, MaintenanceRepository maintenanceRepository) {
        this.assetDb = assetDb;
        this.maintenanceRepository = maintenanceRepository;
    }

    private AssetListResponseDTO listAssetToAssetResponseDTO(Asset asset) {
        AssetListResponseDTO assetResponseDTO = new AssetListResponseDTO();
        assetResponseDTO.setPlatNomor(asset.getPlatNomor());
        assetResponseDTO.setNama(asset.getNama());
        assetResponseDTO.setTipeAset(asset.getJenisAset());
        assetResponseDTO.setStatus(asset.getStatus());
        assetResponseDTO.setNilaiPerolehan(asset.getNilaiPerolehan());
        assetResponseDTO.setSupplierId(asset.getIdSupplier());
        assetResponseDTO.setTanggalPerolehan(asset.getTanggalPerolehan());
        assetResponseDTO.setLastMaintenance(getLastMaintenanceDate(asset.getPlatNomor()));
        
        return assetResponseDTO;
    }

    @Override
    public List<AssetListResponseDTO> getAllAsset() {

        var listAsset = assetDb.findAllActive();
        var listAssetResponseDTO = new ArrayList<AssetListResponseDTO>();
        listAsset.forEach(asset -> {
            var assetResponseDTO = listAssetToAssetResponseDTO(asset);
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
        assetResponseDTO.setFotoContentType(asset.getFotoContentType());
        assetResponseDTO.setFotoUrl("/api/asset/" + asset.getPlatNomor() + "/foto");
        assetResponseDTO.setSupplierId(asset.getIdSupplier());
        
        return assetResponseDTO;
    }

    @Override
    public AssetResponseDTO addAsset(AssetAddDTO assetTempDTO) {
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
                assetTemp.setFoto(assetTempDTO.getFoto().getBytes());
                assetTemp.setFotoContentType(assetTempDTO.getFoto().getContentType());
            } catch (IOException e) {
                throw new IllegalArgumentException("Gagal mengupload foto");
            }
        }

        // Convert String supplierId to UUID
        if (assetTempDTO.getSupplierId() != null && !assetTempDTO.getSupplierId().isEmpty()) {
            try {
                UUID supplierId = UUID.fromString(assetTempDTO.getSupplierId());
                assetTemp.setIdSupplier(supplierId);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Format Supplier ID tidak valid");
            }
        } else {
            throw new IllegalArgumentException("Supplier ID tidak boleh kosong");
        }

        Asset newAssetTemp = assetDb.save(assetTemp);
        return assetToAssetResponseDTO(newAssetTemp);
    }

    @Override
    public Asset getAssetFoto(String id) {
        return assetDb.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
    }
    
    @Override
    public List<AssetResponseDTO> getAssetsBySupplier(UUID supplierId) {
        List<Asset> assets = assetDb.findByIdSupplierAndIsDeletedFalse(supplierId);
        return assets.stream()
                .map(this::assetToAssetResponseDTO)
                .collect(Collectors.toList());
    }

    private Date getLastMaintenanceDate(String platNomor) {
        List<Maintenance> maintenances = maintenanceRepository
            .findByAssetPlatNomorAndStatus(platNomor, "Selesai");
            
        if (maintenances.isEmpty()) {
            return null;
        }
    
        // Cari maintenance terakhir
        return maintenances.stream()
            .map(Maintenance::getTanggalMulaiMaintenance)
            .max(Date::compareTo)
            .orElse(null);
    }
}