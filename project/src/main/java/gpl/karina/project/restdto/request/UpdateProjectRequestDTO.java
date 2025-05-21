package gpl.karina.project.restdto.request;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import gpl.karina.project.restdto.AssetUsageDTO;
import gpl.karina.project.restdto.ResourceUsageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateProjectRequestDTO {
    @NotNull(message = "ID Proyek tidak boleh kosong")
    private String id;

    // Project status for validation logic
    private Integer projectStatus;

    private String projectDescription;

    List<AssetUsageDTO> projectUseAsset;
    List<ResourceUsageDTO> projectUseResource;

    // Required based on project status
    @NotNull(message = "Alamat pengiriman tidak boleh kosong")
    private String projectDeliveryAddress;

    private String projectPickupAddress;

    private Integer projectPHLCount;

    private Long projectPHLPay;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date projectStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date projectEndDate;

    private Long projectTotalPemasukkan;

    private Long projectTotalPengeluaran;
}
