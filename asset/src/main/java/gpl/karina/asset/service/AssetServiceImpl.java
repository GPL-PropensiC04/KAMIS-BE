package gpl.karina.asset.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Date;
import java.text.ParseException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import gpl.karina.asset.dto.response.AssetListResponseDTO;
import gpl.karina.asset.dto.response.AssetResponseDTO;
import gpl.karina.asset.dto.request.AssetAddDTO;
import gpl.karina.asset.dto.request.AssetUpdateRequestDTO;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.repository.MaintenanceRepository;
import gpl.karina.asset.model.Asset;
import gpl.karina.asset.model.Maintenance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetDb assetDb;
    private final MaintenanceRepository maintenanceRepository;
    private final FileStorageService fileStorageService;

    public AssetServiceImpl(AssetDb assetDb, 
            MaintenanceRepository maintenanceRepository, FileStorageService fileStorageService) {
        this.assetDb = assetDb;
        this.maintenanceRepository = maintenanceRepository;
        this.fileStorageService = fileStorageService;
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
            Asset asset = optionalAsset.get();

            // Delete associated file if exists
            if (asset.getFotoFilename() != null && !asset.getFotoFilename().isEmpty()) {
                fileStorageService.deleteFile(asset.getFotoFilename());
            }

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

            // Not implemented as this method is no longer needed with file storage approach
            // Would need to be reimplemented with MultipartFile instead of byte[]

            return assetToAssetResponseDTO(asset);
        } else {
            throw new Exception("Asset tidak ditemukan");
        }
    }

    // Update implementation to work with asset details
    @Override
    @Transactional
    public AssetResponseDTO updateAssetDetails(String platNomor, AssetUpdateRequestDTO updateRequest) throws Exception {
        Optional<Asset> optionalAsset = assetDb.findById(platNomor);
        Asset exisitingAsset;

        if (optionalAsset.isPresent()) {
            exisitingAsset = optionalAsset.get();
            exisitingAsset.setNama(updateRequest.getNama());
            exisitingAsset.setJenisAset(updateRequest.getJenisAset());
            exisitingAsset.setDeskripsi(updateRequest.getDeskripsi());
            exisitingAsset.setStatus(updateRequest.getStatus());

            if (updateRequest.getFoto() != null && !updateRequest.getFoto().isEmpty()) {
                // Delete old file if exists
                if (exisitingAsset.getFotoFilename() != null && !exisitingAsset.getFotoFilename().isEmpty()) {
                    fileStorageService.deleteFile(exisitingAsset.getFotoFilename());
                }

                // Store new file
                String filename = fileStorageService.storeFile(updateRequest.getFoto(), platNomor);
                exisitingAsset.setFotoFilename(filename);
                exisitingAsset.setFotoContentType(updateRequest.getFoto().getContentType());
            }

            assetDb.save(exisitingAsset);
            return assetToAssetResponseDTO(exisitingAsset);
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

        // Set foto URL only if foto exists
        if (asset.getFotoFilename() != null && !asset.getFotoFilename().isEmpty()) {
            assetResponseDTO.setFotoUrl("/api/asset/" + asset.getPlatNomor() + "/foto");
        }

        assetResponseDTO.setSupplierId(asset.getIdSupplier());

        return assetResponseDTO;
    }

    @Override
    public AssetResponseDTO addAsset(AssetAddDTO assetTempDTO) {
        // Validate required fields
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

        try {
            assetTemp.setTanggalPerolehan(new SimpleDateFormat("yyyy-MM-dd").parse(assetTempDTO.getTanggalPerolehan()));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Format tanggal tidak valid, gunakan yyyy-MM-dd");
        }

        assetTemp.setStatus(assetTempDTO.getStatus());
        assetTemp.setIsDeleted(false);

        // Handle file upload
        if (assetTempDTO.getFoto() != null && !assetTempDTO.getFoto().isEmpty()) {
            try {
                // Store the file and save the file name
                String filename = fileStorageService.storeFile(assetTempDTO.getFoto(), assetTempDTO.getPlatNomor());
                assetTemp.setFotoFilename(filename);
                assetTemp.setFotoContentType(assetTempDTO.getFoto().getContentType());
            } catch (Exception e) {
                throw new IllegalArgumentException("Gagal mengupload foto: " + e.getMessage());
            }
        }

        // Handle supplier ID
        if (assetTempDTO.getSupplierId() != null && !assetTempDTO.getSupplierId().isEmpty()) {
            try {
                UUID supplierId = UUID.fromString(assetTempDTO.getSupplierId());
                assetTemp.setIdSupplier(supplierId);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Format Supplier ID tidak valid");
            }
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