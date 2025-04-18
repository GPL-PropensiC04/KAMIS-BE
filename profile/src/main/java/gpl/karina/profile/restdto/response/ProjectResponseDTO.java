package gpl.karina.profile.restdto.response;

import lombok.Data;

@Data
public class ProjectResponseDTO {
    private String id;
    private String projectName;
    private String projectStatus;
    private Long projectTotalPemasukkan;
    private Long projectTotalPengeluaran;
    private Long profit;

    public void calculateProfit() {
        if (projectTotalPemasukkan != null && projectTotalPengeluaran != null) {
            this.profit = projectTotalPemasukkan - projectTotalPengeluaran;
        } else {
            this.profit = null;
        }
    }
}
