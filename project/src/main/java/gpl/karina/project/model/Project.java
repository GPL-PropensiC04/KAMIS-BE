package gpl.karina.project.model;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project")
public class Project {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(nullable = false, name = "tipe_proyek")
    private Boolean projectType; // Value 0 = Penjualan, Value 1 = Pengiriman
    @Column(nullable = false, name = "status_proyek")
    private String projectStatus; // Status yang mungkin direncanakan, dilaksanakan, selesai, telah dibayar
    @Column(nullable = false, name = "nama_proyek")
    private String projectName;
    @Column(name = "deskripsi_proyek")
    private String projectDescription;

    @Column(nullable = false, name = "id_klien")
    private String projectClientId;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "id_aset_yang_digunakan")
    List<ProjectAssetUsage> projectUseAsset;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "id_resource_yang_digunakan")
    List<ProjectResourceUsage> projectUseResource;
    
    @Column(nullable = false, name = "alamat_pengiriman")
    private String projectDeliveryAddress;
    @Column(name = "alamat_pengambilan")
    private String projectPickupAddress;

    @Column(name = "jumlah_phl_yang_dipekerjakan")
    private Integer projectPHLCount;

    @CreationTimestamp
    @Column(name = "tanggal_pembuatan_proyek")
    private Date createdDate;

    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tanggal_mulai_proyek")
    private Date projectStartDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tanggal_selesai_proyek")
    private Date projectEndDate;

    @Column(name = "total_pemasukkan")
    private Long projectTotalPemasukkan;
    @Column(name = "total_pengeluaran")
    private Long projectTotalPengeluaran;
}
