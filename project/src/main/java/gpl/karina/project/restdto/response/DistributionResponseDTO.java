package gpl.karina.project.restdto.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import gpl.karina.project.restdto.AssetUsageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DistributionResponseDTO {

    private String id;
    private Boolean projectType; // Value 0 = Penjualan, Value 1 = Pengiriman
    private Integer projectPaymentStatus; // Status yang mungkin direncanakan, dilaksanakan, selesai, telah dibayar
    private Integer projectStatus; // Status yang mungkin direncanakan, dilaksanakan, selesai, telah dibayar

    private String projectName;

    private String projectDescription;

    private String projectClientId;

    private String projectClientName;

    List<AssetUsageDTO> projectUseAsset;

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

    private List<LogProjectResponseDTO> projectLogs;
    private Date projectPaymentDate;
}
