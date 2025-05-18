package gpl.karina.finance.report.service;

import java.util.Date;
import java.util.List;

import gpl.karina.finance.report.dto.response.ChartPengeluaranResponseDTO;
import gpl.karina.finance.report.dto.response.IncomeExpenseLineResponseDTO;
import gpl.karina.finance.report.dto.response.LapkeuResponseDTO;

public interface LapkeuService {
    List<LapkeuResponseDTO> fetchAllLapkeu(Date startDate, Date endDate, Integer activityType);
    LapkeuResponseDTO createLapkeu(LapkeuResponseDTO lapkeuDTO);
    void deleteLapkeu(String id);
    List<ChartPengeluaranResponseDTO> getPengeluaranChartData(Date startDate, Date endDate);
    List<IncomeExpenseLineResponseDTO> getIncomeExpenseLineChart(String periodType, Date startDate, Date endDate);
}
