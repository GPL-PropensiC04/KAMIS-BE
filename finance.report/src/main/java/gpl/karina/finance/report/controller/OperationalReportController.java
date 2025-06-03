package gpl.karina.finance.report.controller;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpl.karina.finance.report.dto.response.ActivityComparisonResponseDTO;
import gpl.karina.finance.report.dto.response.BaseResponseDTO;
import gpl.karina.finance.report.service.OperationalReportService;

@RestController
@RequestMapping("/api/operational-report")
public class OperationalReportController {
    private final OperationalReportService operationalReportService;

    public OperationalReportController(OperationalReportService operationalReportService) {
        this.operationalReportService = operationalReportService;
    }

    @GetMapping("/activity-chart")
    public ResponseEntity<BaseResponseDTO<List<ActivityComparisonResponseDTO>>> getActivityComparisonChart(
        @RequestParam(name = "range", defaultValue = "THIS_YEAR") String range,
        @RequestParam(name = "periodType", required = false) String periodType,
        @RequestParam(name = "status", defaultValue = "ALL") String status
    ) {
        BaseResponseDTO<List<ActivityComparisonResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<ActivityComparisonResponseDTO> data = operationalReportService
                    .fetchCombinedActivityLineChart(range, periodType, status);

            if (data.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada data aktivitas gabungan yang ditemukan");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan data aktivitas gabungan per periode");
            response.setTimestamp(new Date());
            response.setData(data);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Parameter tidak valid: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan saat mengambil data aktivitas gabungan: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
