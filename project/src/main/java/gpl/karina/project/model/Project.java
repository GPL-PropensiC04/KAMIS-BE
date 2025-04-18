package gpl.karina.project.model;

import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "jenis_proyek", discriminatorType = DiscriminatorType.STRING)
@Table(name = "project")
public class Project {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(nullable = false, name = "tipe_proyek")// Value 0 = Penjualan, Value 1 = Pengiriman
    private Boolean projectType; 
    @Column(name = "status_pembayaran")// Status yang belum lunas, telah lunas
    private String projectPaymentStatus; 
    @Column(nullable = false, name = "status_proyek")
    private String projectStatus; // Status yang mungkin direncanakan, dilaksanakan, selesai
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

    
}
