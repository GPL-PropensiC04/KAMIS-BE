package gpl.karina.asset.service;

import gpl.karina.asset.dto.request.AddLapkeuDTO;
import gpl.karina.asset.dto.request.MaintenanceRequestDTO;
import gpl.karina.asset.dto.response.MaintenanceResponseDTO;
import gpl.karina.asset.model.Asset;
import gpl.karina.asset.model.Maintenance;
import gpl.karina.asset.repository.AssetDb;
import gpl.karina.asset.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Value("${asset.app.financeUrl}")
    private String financeUrl;

    private final MaintenanceRepository maintenanceRepository;
    private final AssetDb assetRepository;
    private final WebClient.Builder webClientBuilder;

    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository, WebClient.Builder webClientBuilder, AssetDb assetRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.assetRepository = assetRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    @Transactional
    public MaintenanceResponseDTO createMaintenance(MaintenanceRequestDTO requestDTO) throws Exception {
        // Menggunakan findById karena AssetDb mungkin menggunakan platNomor sebagai id
        Optional<Asset> assetOptional = assetRepository.findById(requestDTO.getPlatNomor());
        
        if (assetOptional.isEmpty()) {
            throw new Exception("Asset dengan plat nomor " + requestDTO.getPlatNomor() + " tidak ditemukan");
        }
        
        Asset asset = assetOptional.get();
        
        // Check if asset is already in maintenance or in project
        if ("Sedang Maintenance".equals(asset.getStatus())) {
            throw new Exception("Asset dengan plat nomor " + requestDTO.getPlatNomor() + " sedang dalam maintenance");
        }

        if ("Dalam Proyek".equals(asset.getStatus())) {
            throw new Exception("Asset dengan plat nomor " + requestDTO.getPlatNomor() + " sedang digunakan dalam proyek");
        }
                
        // Ubah status asset menjadi sedang maintenance
        asset.setStatus("Sedang Maintenance");
        assetRepository.save(asset);
        
        // Buat record maintenance baru
        Maintenance maintenance = new Maintenance();
        maintenance.setDeskripsiPekerjaan(requestDTO.getDeskripsiPekerjaan());
        maintenance.setBiaya(requestDTO.getBiaya());
        maintenance.setTanggalMulaiMaintenance(requestDTO.getTanggalMulaiMaintenance());
        maintenance.setStatus("Sedang Maintenance");
        maintenance.setAsset(asset);
        
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);

        try {
            AddLapkeuDTO lapkeuRequest = new AddLapkeuDTO();
            lapkeuRequest.setId(savedMaintenance.getId().toString());
            lapkeuRequest.setActivityType(3); // PURCHASE
            lapkeuRequest.setPemasukan(0L);
            lapkeuRequest.setPengeluaran(requestDTO.getBiaya() != null ? requestDTO.getBiaya().longValue() : 0L);
            lapkeuRequest.setDescription("Maintenance - " + asset.getPlatNomor());
            lapkeuRequest.setPaymentDate(savedMaintenance.getTanggalMulaiMaintenance());

            webClientBuilder.build()
                .post()
                .uri(financeUrl + "/api/lapkeu/add") // ganti port sesuai service lapkeu
                .bodyValue(lapkeuRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch (Exception e) {
            System.err.println("Gagal insert ke Lapkeu: " + e.getMessage());
        }
        
        return convertToDTO(savedMaintenance);
    }

    @Override
    public List<MaintenanceResponseDTO> getAllMaintenance() {
        List<Maintenance> maintenances = maintenanceRepository.findAll();
        return maintenances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceResponseDTO> getMaintenanceByAssetIdAndStatus(String platNomor, String status) throws Exception {
        // Verifikasi keberadaan asset terlebih dahulu
        Optional<Asset> assetOptional = assetRepository.findById(platNomor);
        
        if (assetOptional.isEmpty()) {
            throw new Exception("Asset dengan plat nomor " + platNomor + " tidak ditemukan");
        }
        
        // Implementasi filter maintenance berdasarkan aset dan status
        List<Maintenance> maintenances = maintenanceRepository.findByAssetPlatNomorAndStatus(platNomor, status);
        return maintenances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceResponseDTO getMaintenanceById(Long id) throws Exception {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new Exception("Maintenance dengan ID " + id + " tidak ditemukan"));
        
        return convertToDTO(maintenance);
    }

    @Override
    @Transactional
    public MaintenanceResponseDTO completeMaintenance(Long id) throws Exception {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new Exception("Maintenance dengan ID " + id + " tidak ditemukan"));
        
        if (!"Sedang Maintenance".equals(maintenance.getStatus())) {
            throw new Exception("Maintenance ini sudah selesai");
        }
        
        // Update status maintenance
        maintenance.setStatus("Selesai");
        maintenance.setTanggalSelesaiMaintenance(new Date());
        
        // Update status asset
        Asset asset = maintenance.getAsset();
        asset.setStatus("Tersedia");
        assetRepository.save(asset);
        
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(updatedMaintenance);
    }
    
    @Override
    public List<MaintenanceResponseDTO> getMaintenanceByAssetId(String platNomor) throws Exception {
        Optional<Asset> assetOptional = assetRepository.findById(platNomor);
        if (assetOptional.isEmpty()) {
            throw new Exception("Asset dengan plat nomor " + platNomor + " tidak ditemukan");
        }
        List<Maintenance> maintenances = maintenanceRepository.findAll().stream()
                .filter(m -> m.getAsset().getPlatNomor().equals(platNomor))
                .collect(Collectors.toList());
        return maintenances.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceResponseDTO> getAssetsInMaintenance() throws Exception {
        // Ambil daftar maintenance yang sedang dalam status "Sedang Maintenance"
        List<Maintenance> maintenances = maintenanceRepository.findByStatus("Sedang Maintenance");

        if (maintenances == null || maintenances.isEmpty()) {
            throw new Exception("Tidak ada aset yang sedang dalam maintenance");
        }

        // Mengonversi list Maintenance ke MaintenanceResponseDTO
        return maintenances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MaintenanceResponseDTO convertToDTO(Maintenance maintenance) {
        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();
        dto.setId(maintenance.getId());
        dto.setTanggalMulaiMaintenance(maintenance.getTanggalMulaiMaintenance());
        dto.setTanggalSelesaiMaintenance(maintenance.getTanggalSelesaiMaintenance());
        dto.setDeskripsiPekerjaan(maintenance.getDeskripsiPekerjaan());
        dto.setBiaya(maintenance.getBiaya());
        dto.setStatus(maintenance.getStatus());
        dto.setPlatNomor(maintenance.getAsset().getPlatNomor());
        dto.setNamaAset(maintenance.getAsset().getNama());
        return dto;
    }
}