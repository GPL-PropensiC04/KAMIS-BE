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
@Table(name = "Asset")

public class Asset {
    @Id
    @NotNull
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "nama", nullable = false)
    private String nama;

    @NotNull
    @Column(name = "deskripsi", nullable = false)
    private String deskripsi;

    @Column(name = "tanggalPerolehan", updatable = false, nullable = false)
    private Date tanggalPerolehan;

    @NotNull
    @Column(name = "nilaiPerolehan", nullable = false)
    private Float nilaiPerolehan;

    @NotNull
    @Column(name = "assetMaintenance", nullable = false)
    private String assetMaintenance;

    @Column(name = "gambarAset", columnDefinition = "BYTEA")
    private byte[] gambarAset;

    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "deletedAt")
    private Date deletedAt;

    // @ManyToMany
    // @JoinTable(
    //     name = "AssetMaintenance",
    //     joinColumns = @JoinColumn(name = "asset_id"),
    //     inverseJoinColumns = @JoinColumn(name = "maintenance_id")
    // )
    // private List<Maintenance> historiMaintenance;
}
