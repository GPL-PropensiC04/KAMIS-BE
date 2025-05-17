package gpl.karina.finance.report.dto.response;

import java.util.Date;

import lombok.Data;

@Data
public class ProjectResponseDTO {
    private String id;
    private String projectName;
    private Integer projectPaymentStatus;
    private Long projectTotalPemasukkan;
    private Long projectTotalPengeluaran;
    private Boolean projectType; 
    private Date projectPaymentDate;
}
