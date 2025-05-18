package gpl.karina.finance.report.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpl.karina.finance.report.dto.response.BaseResponseDTO;
import gpl.karina.finance.report.dto.response.LapkeuResponseDTO;
import gpl.karina.finance.report.service.LapkeuService;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/lapkeu")
public class LapkeuController {
    private final LapkeuService lapkeuService;

    public LapkeuController(LapkeuService lapkeuService) {
        this.lapkeuService = lapkeuService;
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<LapkeuResponseDTO>>> getAllLapkeu(
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate
    ) {
        BaseResponseDTO<List<LapkeuResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<LapkeuResponseDTO> listLapkeu = lapkeuService.fetchAllLapkeu(startDate, endDate);
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
    
}
