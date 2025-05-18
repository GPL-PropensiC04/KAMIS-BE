package gpl.karina.finance.report.service;

import java.util.Date;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import gpl.karina.finance.report.dto.response.AssetTempResponseDTO;
import gpl.karina.finance.report.dto.response.BaseResponseDTO;
import gpl.karina.finance.report.dto.response.ChartPengeluaranResponseDTO;
import gpl.karina.finance.report.dto.response.IncomeExpenseLineResponseDTO;
import gpl.karina.finance.report.dto.response.LapkeuResponseDTO;
import gpl.karina.finance.report.model.Lapkeu;
import gpl.karina.finance.report.repository.LapkeuRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class LapkeuServiceImpl implements LapkeuService {

    private final LapkeuRepository lapkeuRepository;

    @Autowired
    private HttpServletRequest request;

    @Value("${finance.report.app.profileUrl}")
    private String profileUrl;

    @Value("${finance.report.app.projectUrl}")
    private String projectUrl;

    @Value("${finance.report.app.purchaseUrl}")
    private String purchaseUrl;

    @Value("${finance.report.app.assetUrl}")
    private String assetUrl;

    public LapkeuServiceImpl(LapkeuRepository lapkeuRepository) {
        this.lapkeuRepository = lapkeuRepository;
    }

    public String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public List<LapkeuResponseDTO> fetchAllLapkeu(Date startDate, Date endDate, Integer activityType) {
        List<Lapkeu> lapkeuList = lapkeuRepository.findAll();
        List<Lapkeu> filtered = lapkeuList.stream()
            .filter(l -> {
                if (startDate != null && l.getPaymentDate() != null && l.getPaymentDate().before(startDate)) {
                    return false;
                }
                if (endDate != null && l.getPaymentDate() != null && l.getPaymentDate().after(endDate)) {
                    return false;
                }
                if (activityType != null && !activityType.equals(l.getActivityType())) {
                    return false;
                }
                return true;
            })
            .collect(Collectors.toList());
            
        return filtered.stream()
            .map(l -> new LapkeuResponseDTO(
                    l.getId(), l.getActivityType(), l.getPemasukan(), l.getPengeluaran(), l.getDescription(), l.getPaymentDate()))
            .collect(Collectors.toList());
    }

    @Override
    public LapkeuResponseDTO createLapkeu(LapkeuResponseDTO lapkeuDTO) {
        Lapkeu lapkeu = new Lapkeu();
        lapkeu.setId(lapkeuDTO.getId());
        lapkeu.setActivityType(lapkeuDTO.getActivityType());
        lapkeu.setPemasukan(lapkeuDTO.getPemasukan());
        lapkeu.setPengeluaran(lapkeuDTO.getPengeluaran());
        lapkeu.setDescription(lapkeuDTO.getDescription());
        lapkeu.setPaymentDate(lapkeuDTO.getPaymentDate());

        try {
            Lapkeu savedLapkeu = lapkeuRepository.save(lapkeu);
            return convertToLapkeuResponseDTO(savedLapkeu);
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error occurred while creating Lapkeu: " + e.getMessage()
            );
        }
    }

    private LapkeuResponseDTO convertToLapkeuResponseDTO(Lapkeu lapkeu) {
        var lapkeuDTO = new LapkeuResponseDTO();
        lapkeuDTO.setId(lapkeu.getId());
        lapkeuDTO.setActivityType(lapkeu.getActivityType());
        lapkeuDTO.setPemasukan(lapkeu.getPemasukan());
        lapkeuDTO.setPengeluaran(lapkeu.getPengeluaran());
        lapkeuDTO.setDescription(lapkeu.getDescription());
        lapkeuDTO.setPaymentDate(lapkeu.getPaymentDate());
        return lapkeuDTO;
    }

    @Override
    public void deleteLapkeu(String id) {
        Lapkeu lapkeu = lapkeuRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lapkeu not found"));
        lapkeuRepository.delete(lapkeu);
    }

    @Override
    public List<ChartPengeluaranResponseDTO> getPengeluaranChartData(Date startDate, Date endDate) {
        List<Object[]> rawData;

        if (startDate != null && endDate != null) {
            rawData = lapkeuRepository.getTotalPengeluaranPerActivityTypeBetweenDates(startDate, endDate);
        } else {
            rawData = lapkeuRepository.getTotalPengeluaranPerActivityType();
        }

        return rawData.stream()
            .map(obj -> new ChartPengeluaranResponseDTO((Integer) obj[0], (Long) obj[1]))
            .collect(Collectors.toList());
    }

    @Override
    public List<IncomeExpenseLineResponseDTO> getIncomeExpenseLineChart(String periodType, Date startDate, Date endDate) {
        List<Object[]> rawData;
        Map<String, IncomeExpenseLineResponseDTO> resultMap = new HashMap<>();

        // Default fallback: tahun ini
        if (startDate == null || endDate == null) {
            LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
            startDate = Date.from(startOfYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
            endDate = new Date();
        }

        List<String> fullPeriods;

        switch (periodType.toUpperCase()) {
            case "MONTHLY":
                rawData = lapkeuRepository.getIncomeExpenseMonthlyFiltered(startDate, endDate);
                for (Object[] row : rawData) {
                    resultMap.put((String) row[0], new IncomeExpenseLineResponseDTO((String) row[0], (Long) row[1], (Long) row[2]));
                }
                fullPeriods = generateMonthPeriods(startDate, endDate);
                break;

            case "QUARTERLY":
                rawData = lapkeuRepository.getIncomeExpenseQuarterlyFiltered(startDate, endDate);
                for (Object[] row : rawData) {
                    resultMap.put((String) row[0], new IncomeExpenseLineResponseDTO((String) row[0], (Long) row[1], (Long) row[2]));
                }
                fullPeriods = generateQuarterPeriods(startDate, endDate);
                break;

            case "YEARLY":
                rawData = lapkeuRepository.getIncomeExpenseYearlyFiltered(startDate, endDate);
                for (Object[] row : rawData) {
                    resultMap.put((String) row[0], new IncomeExpenseLineResponseDTO((String) row[0], (Long) row[1], (Long) row[2]));
                }
                fullPeriods = generateYearPeriods(startDate, endDate);
                break;

            default:
                throw new IllegalArgumentException("Invalid periodType: " + periodType);
        }

        return fullPeriods.stream()
            .map(period -> resultMap.getOrDefault(period, new IncomeExpenseLineResponseDTO(period, 0L, 0L)))
            .collect(Collectors.toList());
    }
    
    private List<String> generateMonthPeriods(Date startDate, Date endDate) {
        List<String> periods = new ArrayList<>();
        LocalDate start = toLocalDate(startDate).withDayOfMonth(1);
        LocalDate end = toLocalDate(endDate).withDayOfMonth(1);

        while (!start.isAfter(end)) {
            periods.add(start.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            start = start.plusMonths(1);
        }
        return periods;
    }

    private List<String> generateQuarterPeriods(Date startDate, Date endDate) {
        List<String> periods = new ArrayList<>();
        LocalDate start = toLocalDate(startDate).withDayOfMonth(1);
        LocalDate end = toLocalDate(endDate).withDayOfMonth(1);

        while (!start.isAfter(end)) {
            int quarter = (start.getMonthValue() - 1) / 3 + 1;
            String period = start.getYear() + "-Q" + quarter;
            if (!periods.contains(period)) {
                periods.add(period);
            }
            start = start.plusMonths(1);
        }
        return periods;
    }

    private List<String> generateYearPeriods(Date startDate, Date endDate) {
        List<String> periods = new ArrayList<>();
        int startYear = toLocalDate(startDate).getYear();
        int endYear = toLocalDate(endDate).getYear();
        for (int year = startYear; year <= endYear; year++) {
            periods.add(String.valueOf(year));
        }
        return periods;
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }



}
