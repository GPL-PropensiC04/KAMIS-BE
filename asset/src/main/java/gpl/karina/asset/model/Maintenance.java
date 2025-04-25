package gpl.karina.asset.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Maintenance")
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "tanggalMulaiMaintenance", nullable = false)
    private Date tanggalMulaiMaintenance;

    @Column(name = "tanggalSelesaiMaintenance")
    private Date tanggalSelesaiMaintenance;

    @NotNull
    @Column(name = "deskripsiPekerjaan", nullable = false)
    private String deskripsiPekerjaan;

    @NotNull
    @Column(name = "biaya", nullable = false)
    private Float biaya;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;
    
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
}