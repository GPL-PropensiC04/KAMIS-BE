package gpl.karina.project.model;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
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
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "jenis_proyek", discriminatorType = DiscriminatorType.STRING)
@Table(name = "project", indexes = {
        @Index(name = "idx_project_client_id", columnList = "id_klien"),
        @Index(name = "idx_project_status", columnList = "status_proyek"),
        @Index(name = "idx_project_type", columnList = "tipe_proyek"),
        @Index(name = "idx_project_start_date", columnList = "tanggal_mulai_proyek"),
        @Index(name = "idx_project_end_date", columnList = "tanggal_selesai_proyek")
    })
public class Project {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(nullable = false, name = "tipe_proyek") // Value 0 = Penjualan, Value 1 = Pengiriman
    private Boolean projectType;
    @Column(name = "status_pembayaran") // 0: belum lunas, 1 : telah lunas, 2 : dikembalikan 
    private Integer projectPaymentStatus;
    @Column(nullable = false, name = "status_proyek")
    private Integer projectStatus; // 0 : Direncanakan, 1 : dilaksanakan, 2 : selesai, 3 : batal
    @Column(nullable = false, name = "nama_proyek")
    private String projectName;
    @Column(name = "deskripsi_proyek")
    private String projectDescription;

    @Column(nullable = false, name = "id_klien")
    private String projectClientId;

    @Column(nullable = false, name = "alamat_pengiriman")
    private String projectDeliveryAddress;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tanggal_pembayaran")
    private Date projectPaymentDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id", nullable = false)
    List<LogProject> projectLogs;
}
