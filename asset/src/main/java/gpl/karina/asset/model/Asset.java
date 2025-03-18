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
    @Column(name = "platNomor", nullable = false)
    private String platNomor; 

    @NotNull
    @Column(name = "nama", nullable = false)
    private String nama;

    @NotNull
    @Column(name = "jenisAset", nullable = false)
    private String jenisAset;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "tanggalPerolehan", updatable = false, nullable = false)
    private Date tanggalPerolehan;

    @NotNull
    @Column(name = "nilaiPerolehan", nullable = false, updatable = false)
    private Float nilaiPerolehan;

    @NotNull
    @Column(name = "deskripsi", nullable = false)
    private String deskripsi;

    @NotNull
    @Column(name = "assetMaintenance", nullable = false)
    private String assetMaintenance;

    @Column(name = "gambarAset", columnDefinition = "BYTEA")
    private byte[] gambarAset;

    @Column(name = "foto_content_type")
    private String fotoContentType;

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
