package gpl.karina.finance.report.dto.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LapkeuResponseDTO {
    private String id;
    private Integer activityType; // 0 : DISTRIBUSI, 1 : PENJUALAN, 2 : PURCHASE, 3 : MAINTENANCE
    private Long pemasukan;
    private Long pengeluaran;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date paymentDate; // Tanggal Pembayaran
}
