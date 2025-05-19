package gpl.karina.finance.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeExpenseLineResponseDTO {
    private String period;
    private Long totalPemasukan;
    private Long totalPengeluaran;
}

