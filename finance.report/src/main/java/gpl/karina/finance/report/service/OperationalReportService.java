package gpl.karina.finance.report.service;

import java.util.Date;
import java.util.List;

import gpl.karina.finance.report.dto.response.ActivityComparisonResponseDTO;

public interface OperationalReportService {
    public List<ActivityComparisonResponseDTO> fetchCombinedActivityLineChart(String startDate, String endDate, String periodType, String status);
}
