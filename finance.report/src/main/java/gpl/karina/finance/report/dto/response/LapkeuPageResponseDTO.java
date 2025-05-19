package gpl.karina.finance.report.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LapkeuPageResponseDTO {
    private LapkeuSummaryResponseDTO summary;
    private List<LapkeuResponseDTO> list;
}