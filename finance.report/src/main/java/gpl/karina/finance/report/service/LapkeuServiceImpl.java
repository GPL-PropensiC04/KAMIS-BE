package gpl.karina.finance.report.service;

import java.util.Date;
import java.util.HashMap;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import gpl.karina.finance.report.dto.response.IncomeExpenseBarResponseDTO;
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
    public List<ChartPengeluaranResponseDTO> getPengeluaranChartData(String range) {
        LocalDate now = LocalDate.now();
        LocalDate start;
        LocalDate end = now;

        // Tentukan rentang tanggal berdasarkan range
        switch (range.toUpperCase()) {
            case "THIS_MONTH":
                start = now.withDayOfMonth(1);
                break;

            case "THIS_QUARTER":
                int quarter = (now.getMonthValue() - 1) / 3 + 1;
                Month firstMonth = Month.of((quarter - 1) * 3 + 1);
                start = LocalDate.of(now.getYear(), firstMonth, 1);
                break;

            case "THIS_YEAR":
            default:
                start = now.withDayOfYear(1);
                break;
        }

        Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        List<Object[]> rawData = lapkeuRepository.getTotalPengeluaranPerActivityTypeBetweenDates(startDate, endDate);

        return rawData.stream()
                .map(obj -> new ChartPengeluaranResponseDTO((Integer) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }


    @Override
    public List<IncomeExpenseLineResponseDTO> getIncomeExpenseLineChart(String periodType, String range) {
        List<Object[]> rawData;
        Map<String, IncomeExpenseLineResponseDTO> resultMap = new HashMap<>();

        LocalDate now = LocalDate.now();
        LocalDate start;
        LocalDate end = now;

        // Tetapkan default periodType berdasarkan range
        if (periodType == null || periodType.isBlank()) {
            switch (range.toUpperCase()) {
                case "THIS_MONTH":
                    periodType = "WEEKLY";
                    break;
                case "THIS_QUARTER":
                    periodType = "MONTHLY";
                    break;
                case "THIS_YEAR":
                default:
                    periodType = "MONTHLY";
                    break;
            }
        }

        // Tentukan start & end date berdasarkan range
        switch (range.toUpperCase()) {
            case "THIS_MONTH":
                if (!periodType.equalsIgnoreCase("WEEKLY")) {
                    throw new IllegalArgumentException("THIS_MONTH hanya mendukung periodType = WEEKLY");
                }
                start = now.withDayOfMonth(1);
                break;

            case "THIS_QUARTER":
                if (!periodType.equalsIgnoreCase("MONTHLY")) {
                    throw new IllegalArgumentException("THIS_QUARTER hanya mendukung periodType = MONTHLY");
                }
                int quarter = (now.getMonthValue() - 1) / 3 + 1;
                Month firstMonth = Month.of((quarter - 1) * 3 + 1);
                start = LocalDate.of(now.getYear(), firstMonth, 1);
                break;

            case "THIS_YEAR":
                if (!periodType.equalsIgnoreCase("MONTHLY") && !periodType.equalsIgnoreCase("QUARTERLY")) {
                    throw new IllegalArgumentException("THIS_YEAR hanya mendukung periodType = MONTHLY atau QUARTERLY");
                }
                start = now.withDayOfYear(1);
                break;

            default:
                throw new IllegalArgumentException("Range tidak valid. Gunakan THIS_YEAR, THIS_QUARTER, atau THIS_MONTH.");
        }

        Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

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

            case "WEEKLY":
            rawData = lapkeuRepository.getIncomeExpenseRawByDay(startDate, endDate);
            int fixedMonth = toLocalDate(startDate).getMonthValue();
            int fixedYear = toLocalDate(startDate).getYear();

            for (Object[] row : rawData) {
                LocalDate date = ((Date) row[0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String period = getMonthWeekLabel(date, fixedMonth, fixedYear);

                resultMap.computeIfAbsent(period, p -> new IncomeExpenseLineResponseDTO(p, 0L, 0L));
                IncomeExpenseLineResponseDTO dto = resultMap.get(period);
                dto.setTotalPemasukan(dto.getTotalPemasukan() + (row[1] != null ? (Long) row[1] : 0L));
                dto.setTotalPengeluaran(dto.getTotalPengeluaran() + (row[2] != null ? (Long) row[2] : 0L));
            }

            fullPeriods = generateMonthWeekPeriods(startDate, endDate);
            break;


            default:
                throw new IllegalArgumentException("Period type tidak dikenali: " + periodType);
        }

        return fullPeriods.stream()
                .map(period -> resultMap.getOrDefault(period, new IncomeExpenseLineResponseDTO(period, 0L, 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public List<IncomeExpenseBarResponseDTO> getIncomeExpenseBarChart(String periodType, String range) {
        List<IncomeExpenseLineResponseDTO> lineData = getIncomeExpenseLineChart(periodType, range);
        List<IncomeExpenseBarResponseDTO> barData = new ArrayList<>();
        for (IncomeExpenseLineResponseDTO dto : lineData) {
            barData.add(new IncomeExpenseBarResponseDTO(dto.getPeriod(), dto.getTotalPemasukan(), dto.getTotalPengeluaran()));
        }
        return barData;
    }

    private String getMonthWeekLabel(LocalDate anyDateInWeek, int fixedMonth, int fixedYear) {
        LocalDate firstDayOfMonth = LocalDate.of(fixedYear, fixedMonth, 1);
        int weekOfMonth = (int) ChronoUnit.WEEKS.between(
                firstDayOfMonth.with(DayOfWeek.MONDAY),
                anyDateInWeek.with(DayOfWeek.MONDAY)
        ) + 1;

        return String.format("%04d-%02d-W%d", fixedYear, fixedMonth, weekOfMonth);
    }


    private List<String> generateMonthWeekPeriods(Date startDate, Date endDate) {
        List<String> periods = new ArrayList<>();
        LocalDate pointer = toLocalDate(startDate).with(DayOfWeek.MONDAY);
        LocalDate end = toLocalDate(endDate);

        int targetMonth = toLocalDate(startDate).getMonthValue();
        int targetYear = toLocalDate(startDate).getYear();

        while (!pointer.isAfter(end)) {
            // Hanya tambahkan minggu yang mengandung hari dari bulan & tahun target
            for (int i = 0; i < 7; i++) {
                LocalDate day = pointer.plusDays(i);
                if (day.getMonthValue() == targetMonth && day.getYear() == targetYear) {
                    String label = getMonthWeekLabel(pointer, targetMonth, targetYear);
                    if (!periods.contains(label)) {
                        periods.add(label);
                    }
                    break;
                }
            }
            pointer = pointer.plusWeeks(1);
        }

        return periods;
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

    // private List<String> generateYearPeriods(Date startDate, Date endDate) {
    //     List<String> periods = new ArrayList<>();
    //     int startYear = toLocalDate(startDate).getYear();
    //     int endYear = toLocalDate(endDate).getYear();
    //     for (int year = startYear; year <= endYear; year++) {
    //         periods.add(String.valueOf(year));
    //     }
    //     return periods;
    // }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
