package gpl.karina.purchase.restdto.response;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSummaryResponseDTO {
    private int totalPurchase;       // jumlah total pembelian saat ini
    private double percentageChange; // persen perubahan dibanding periode sebelumnya
}
