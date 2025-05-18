package gpl.karina.finance.report.service;

import java.util.Date;
import java.util.List;

import gpl.karina.finance.report.dto.response.LapkeuResponseDTO;

public interface LapkeuService {
    List<LapkeuResponseDTO> fetchAllLapkeu(Date startDate, Date endDate, Integer activityType);
}
