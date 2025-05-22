package gpl.karina.asset.dto.request;

import java.util.Date;
import lombok.Data;

@Data
public class AddLapkeuDTO {
    private String id;             // ID transaksi (purchaseId)
    private Integer activityType;  // 2 untuk PURCHASE
    private Long pemasukan;        // 0 untuk purchase
    private Long pengeluaran;      // purchase price
    private String description;    // keterangan
    private Date paymentDate;      // tanggal pembayaran
}
