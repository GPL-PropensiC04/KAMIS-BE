package gpl.karina.finance.report.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityComparisonResponseDTO {
    private String period;
    private Long pembelianCount;
    private Long penjualanCount;
    private Long distribusiCount;

    public ActivityComparisonResponseDTO(String period) {
        this.period = period;
    }
}
