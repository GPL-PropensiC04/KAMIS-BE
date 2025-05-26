package gpl.karina.finance.report.controller;

import gpl.karina.finance.report.dto.response.BaseResponseDTO;
import gpl.karina.finance.report.dto.response.FinancialSummaryResponseDTO;
import gpl.karina.finance.report.service.FinancialReportService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/finance-report")
public class FinanceReportController {

    private final FinancialReportService financialReportService;

    public FinanceReportController(FinancialReportService financialReportService) {
        this.financialReportService = financialReportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<BaseResponseDTO<FinancialSummaryResponseDTO>> getFinancialSummary(
            @RequestParam(name = "range", defaultValue = "THIS_MONTH") String range) {

        BaseResponseDTO<FinancialSummaryResponseDTO> response = new BaseResponseDTO<>();
        try {
            FinancialSummaryResponseDTO financialSummary = financialReportService.getFinancialSummary(range);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan laporan keuangan");
            response.setTimestamp(new Date());
            response.setData(financialSummary);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan saat mengambil laporan keuangan: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
