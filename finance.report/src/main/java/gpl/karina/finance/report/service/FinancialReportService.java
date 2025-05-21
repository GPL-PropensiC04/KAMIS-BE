package gpl.karina.finance.report.service;

import gpl.karina.finance.report.dto.response.FinancialSummaryResponseDTO;

public interface FinancialReportService {
    FinancialSummaryResponseDTO getFinancialSummary(String range) throws Exception;
}
