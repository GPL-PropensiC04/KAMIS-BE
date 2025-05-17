package gpl.karina.finance.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Lapkeu")
public class Lapkeu {
    @Id
    @Column(name = "id", nullable = false)
    private String id; // ID aktivitas (distribusi/penjualan/purchasing/maintenance)

    @Column(name = "jenis_aktivitas", nullable = false)
    private Integer activityType; // 0 : PENJUALAN, 1 : DISTRIBUSI, 2 : PURCHASE, 3 : MAINTENANCE

    @Column
    private Long pemasukan; // Null jika tidak ada pemasukan

    @Column
    private Long pengeluaran; // Null jika tidak ada pengeluaran

    @Column
    private String description; // Keterangan tambahan (opsional)
}
