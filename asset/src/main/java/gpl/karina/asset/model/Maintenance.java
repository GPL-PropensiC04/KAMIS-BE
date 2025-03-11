package gpl.karina.asset.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

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
    @Column(name = "tanggalMaintenance", nullable = false)
    private Date tanggalMaintenance;

    @NotNull
    @Column(name = "deskripsiPekerjaan", nullable = false)
    private String deskripsiPekerjaan;

    @NotNull
    @Column(name = "biaya", nullable = false)
    private Float biaya;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @ManyToMany(mappedBy = "historiMaintenance")
    private List<Asset> assets;
}