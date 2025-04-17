package gpl.karina.project.restdto.response;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectResponseDTO {
    
    private String id;
    private Boolean projectType; // Value 0 = Penjualan, Value 1 = Pengiriman
    private String projectStatus; // Status yang mungkin direncanakan, dilaksanakan, selesai, telah dibayar
    
    private String projectName;
    
    private String projectDescription;

    
    private String projectClientId;

    
    List<String> projectUseAsset;

    
    List<String> projectUseResource;
    
    
    private String projectDeliveryAddress;
    
    private String projectPickupAddress;

    
    private Integer projectPHLCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")
    private Date projectStartDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone="Asia/Jakarta")

    private Date projectEndDate;
}
