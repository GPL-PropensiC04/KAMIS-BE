package gpl.karina.project.restdto.response;

import java.util.Date;
import java.util.List;



import com.fasterxml.jackson.annotation.JsonFormat;


import gpl.karina.project.restdto.AssetUsageDTO;
import gpl.karina.project.restdto.ResourceUsageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectResponseWrapperDTO {
    
    private Boolean projectType; // Value 0 = Penjualan, Value 1 = Pengiriman
    private Object data; // Data bisa berupa SellResponseDTO atau DistributionResponseDTO

    public static ProjectResponseWrapperDTO fromSellResponse(SellResponseDTO sellResponse) {
        ProjectResponseWrapperDTO wrapper = new ProjectResponseWrapperDTO();
        wrapper.setProjectType(false); // false for Sell
        wrapper.setData(sellResponse);
        return wrapper;
    }
    public static ProjectResponseWrapperDTO fromDistributionResponse(DistributionResponseDTO distributionResponse) {
        ProjectResponseWrapperDTO wrapper = new ProjectResponseWrapperDTO();
        wrapper.setProjectType(true); // true for Distribution
        wrapper.setData(distributionResponse);
        return wrapper;
    }
}
