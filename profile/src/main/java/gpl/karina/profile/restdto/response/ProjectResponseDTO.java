package gpl.karina.profile.restdto.response;

import java.util.Date;

import lombok.Data;

@Data
public class ProjectResponseDTO {
    private String id;
    private String projectName;
    private String projectStatus;
    private Long projectTotalPemasukkan;
    private Long projectTotalPengeluaran;
    private Boolean projectType; 
    private Date projectStartDate;
    private Long profit;

    public void calculateProfit() {
        if (projectTotalPemasukkan != null && projectTotalPengeluaran != null) {
            this.profit = projectTotalPemasukkan - projectTotalPengeluaran;
        } else {
            this.profit = null;
        }
    }
}
