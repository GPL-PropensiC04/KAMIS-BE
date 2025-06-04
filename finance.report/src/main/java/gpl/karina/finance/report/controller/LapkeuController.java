package gpl.karina.finance.report.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpl.karina.finance.report.dto.response.BaseResponseDTO;
import gpl.karina.finance.report.dto.response.ChartPengeluaranResponseDTO;
import gpl.karina.finance.report.dto.response.IncomeExpenseBarResponseDTO;
import gpl.karina.finance.report.dto.response.IncomeExpenseBreakdownDTO;
import gpl.karina.finance.report.dto.response.IncomeExpenseLineResponseDTO;
import gpl.karina.finance.report.dto.response.LapkeuPageResponseDTO;
import gpl.karina.finance.report.dto.response.LapkeuResponseDTO;
import gpl.karina.finance.report.dto.response.LapkeuSummaryResponseDTO;
import gpl.karina.finance.report.service.LapkeuService;
import gpl.karina.finance.report.repository.LapkeuRepository;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/lapkeu")
public class LapkeuController {
    private final LapkeuService lapkeuService;
    private final LapkeuRepository lapkeuRepository;

    public LapkeuController(LapkeuService lapkeuService, LapkeuRepository lapkeuRepository) {
        this.lapkeuService = lapkeuService;
        this.lapkeuRepository = lapkeuRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<LapkeuResponseDTO>>> getAllLapkeu(
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
        @RequestParam(name = "activityType", required = false) Integer activityType
    ) {
        BaseResponseDTO<List<LapkeuResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<LapkeuResponseDTO> listLapkeu = lapkeuService.fetchAllLapkeu(startDate, endDate, activityType);
            if (listLapkeu.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada data laporan keuangan yang ditemukan");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan daftar laporan keuangan");
            response.setTimestamp(new Date());
            response.setData(listLapkeu);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan saat mengambil data laporan keuangan: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> createLapkeu(@RequestBody LapkeuResponseDTO lapkeuDTO) {
        var baseResponseDTO = new BaseResponseDTO<LapkeuResponseDTO>();
        try {
            LapkeuResponseDTO lapkeu = lapkeuService.createLapkeu(lapkeuDTO);
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(lapkeu);
            baseResponseDTO.setMessage("Lapkeu berhasil dibuat");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Gagal membuat Lapkeu: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> deleteLapkeu(@PathVariable String id) {
        System.out.println("Delete Lapkeu called with id: " + id);
        BaseResponseDTO<Void> response = new BaseResponseDTO<>();
        try {
            lapkeuService.deleteLapkeu(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil menghapus data laporan keuangan");
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Gagal menghapus data laporan keuangan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/chart-pengeluaran")
    public ResponseEntity<BaseResponseDTO<List<ChartPengeluaranResponseDTO>>> getPengeluaranPerAktivitas(
        @RequestParam(name = "range", defaultValue = "THIS_YEAR") String range
    ) {
        BaseResponseDTO<List<ChartPengeluaranResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<ChartPengeluaranResponseDTO> listPengeluaran = lapkeuService.getPengeluaranChartData(range);

            if (listPengeluaran.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada data pengeluaran yang ditemukan");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan daftar pengeluaran per aktivitas");
            response.setTimestamp(new Date());
            response.setData(listPengeluaran);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Parameter tidak valid: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan saat mengambil data pengeluaran: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/chart-pemasukan-pengeluaran")
    public ResponseEntity<BaseResponseDTO<List<IncomeExpenseLineResponseDTO>>> getPemasukanPengeluaranPerPeriode(
        @RequestParam(name = "range", defaultValue = "THIS_YEAR") String range,
        @RequestParam(name = "periodType", required = false) String periodType
    ) {
        BaseResponseDTO<List<IncomeExpenseLineResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<IncomeExpenseLineResponseDTO> data = lapkeuService.getIncomeExpenseLineChart(periodType, range);

            if (data.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada data yang ditemukan");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan daftar pemasukan dan pengeluaran per periode");
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
            response.setMessage("Terjadi kesalahan saat mengambil data: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/chart-total-pemasukan-pengeluaran")
    public ResponseEntity<BaseResponseDTO<List<IncomeExpenseBreakdownDTO>>> getBreakdownPemasukanPengeluaran(
        @RequestParam(name = "range", defaultValue = "THIS_YEAR") String range,
        @RequestParam(name = "periodType", required = false) String periodType
    ) {
        BaseResponseDTO<List<IncomeExpenseBreakdownDTO>> response = new BaseResponseDTO<>();
        try {
            List<IncomeExpenseBreakdownDTO> data = lapkeuService.getIncomeExpenseBreakdown(periodType, range);

            if (data.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Tidak ada data yang ditemukan");
                response.setTimestamp(new Date());
                response.setData(null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan breakdown pemasukan dan pengeluaran per jenis aktivitas");
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
            response.setMessage("Terjadi kesalahan saat mengambil data: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<BaseResponseDTO<LapkeuSummaryResponseDTO>> getLapkeuSummary(
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
        @RequestParam(name = "activityType", required = false) Integer activityType
    ) {
        BaseResponseDTO<LapkeuSummaryResponseDTO> response = new BaseResponseDTO<>();
        try {
            LapkeuSummaryResponseDTO summary = lapkeuService.getLapkeuSummary(startDate, endDate, activityType);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan summary laporan keuangan");
            response.setTimestamp(new Date());
            response.setData(summary);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/page")
    public ResponseEntity<BaseResponseDTO<LapkeuPageResponseDTO>> getLapkeuPage(
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
        @RequestParam(name = "activityType", required = false) Integer activityType
    ) {
        BaseResponseDTO<LapkeuPageResponseDTO> response = new BaseResponseDTO<>();
        try {
            LapkeuSummaryResponseDTO summary = lapkeuService.getLapkeuSummary(startDate, endDate, activityType);
            List<LapkeuResponseDTO> list = lapkeuService.fetchAllLapkeu(startDate, endDate, activityType);
            LapkeuPageResponseDTO page = new LapkeuPageResponseDTO(summary, list);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Berhasil mendapatkan data laporan keuangan");
            response.setTimestamp(new Date());
            response.setData(page);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
