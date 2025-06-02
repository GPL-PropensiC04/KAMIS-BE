package gpl.karina.asset.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "asset_maintenance")
public class AssetMaintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @NotNull
    @Column(name = "plat_nomor", nullable = false)
    private String platNomor;

    @NotNull
    @Column(name = "tanggalMulaiMaintenance", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date tanggalMulaiMaintenance;

    @Column(name = "tanggalSelesaiMaintenance")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tanggalSelesaiMaintenance;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status; // "Dijadwalkan", "Sedang Berlangsung", "Selesai", "Batal"

    @NotNull
    @Column(name = "deskripsiPekerjaan", nullable = false)
    private String deskripsiPekerjaan;

    @NotNull
    @Column(name = "biaya", nullable = false)
    private Float biaya;

    @Column(name = "notes")
    private String notes;

    @NotNull
    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plat_nomor", referencedColumnName = "platNomor", insertable = false, updatable = false)
    private Asset asset;
}
