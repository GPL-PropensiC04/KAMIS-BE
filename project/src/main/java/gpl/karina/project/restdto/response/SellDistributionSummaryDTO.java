package gpl.karina.project.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellDistributionSummaryDTO {
    private Long totalSell;
    private Double percentageSellChange;
    private Long totalDistribution;
    private Double percentageDistributionChange;
}
