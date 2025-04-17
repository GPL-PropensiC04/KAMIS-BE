package gpl.karina.project.restdto.request;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import gpl.karina.project.restdto.AssetUsageDTO;
import gpl.karina.project.restdto.ResourceUsageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectRequestDTO {
    @NotNull(message = "Tipe Proyek tidak boleh kosong")
    private Boolean projectType; // Value 0 = Penjualan, Value 1 = Pengiriman
    
    // private String projectStatus; // Status yang mungkin direncanakan, dilaksanakan, selesai, telah dibayar
    @NotNull(message = "Nama Proyek tidak boleh kosong")
    private String projectName;
    
    private String projectDescription;
    @NotNull(message = "ID Klien tidak boleh kosong")
    private String projectClientId;

    List<AssetUsageDTO> projectUseAsset;
    List<ResourceUsageDTO> projectUseResource;

    @NotNull(message = "Alamat pengiriman tidak boleh kosong")
    private String projectDeliveryAddress;
    
    private String projectPickupAddress;

    private Integer projectPHLCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date projectStartDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date projectEndDate;

    
    private Long projectTotalPemasukkan;
    
    private Long projectTotalPengeluaran;
}
