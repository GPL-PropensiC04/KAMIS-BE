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
@Table(name = "asset_reservation")
public class AssetReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @NotNull
    @Column(name = "plat_nomor", nullable = false)
    private String platNomor;

    @NotNull
    @Column(name = "project_id", nullable = false)
    private String projectId;

    @NotNull
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @NotNull
    @Column(name = "status", nullable = false)
    private String reservationStatus; // "Direncanakan", "Dilaksanakan", "Selesai", "Batal"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plat_nomor", referencedColumnName = "platNomor", insertable = false, updatable = false)
    private Asset asset;
}