package gpl.karina.finance.report.dto.response;

import lombok.Data;

@Data
public class ProjectResponseDTO {
    private String id;
    private String projectName;
    private String projectStatus;
    private Long projectTotalPemasukkan;
    private Long projectTotalPengeluaran;
    private Boolean projectType; 
}
