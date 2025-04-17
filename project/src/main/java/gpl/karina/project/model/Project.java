package gpl.karina.project.model;

import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    @Column(nullable = false, name = "Tipe Proyek")
    private Boolean projectType; // Value 0 = Penjualan, Value 1 = Pengiriman
    @Column(nullable = false, name = "Status Proyek")
    private String projectStatus; // Status yang mungkin direncanakan, dilaksanakan, selesai, telah dibayar
    @Column(nullable = false, name = "Nama Proyek")
    private String projectName;
    @Column(name = "Deskripsi Proyek")
    private String projectDescription;

    @Column(nullable = false, name = "ID Klien")
    private String projectClientId;

    @Column(name = "ID Aset yang digunakan")
    List<String> projectUseAsset;

    @Column(name = "ID Resource yang digunakan")
    List<String> projectUseResource;
    
    @Column(nullable = false, name = "Alamat pengiriman")
    private String projectDeliveryAddress;
    @Column(name = "Alamat pengambilan")
    private String projectPickupAddress;

    @Column(name = "Jumlah PHL yang dipekerjakan")
    private Integer projectPHLCount;

    @CreationTimestamp
    @Column(name = "Tanggal Pembuatan Proyek")
    private Date createdDate;

    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Tanggal Mulai Proyek")
    private Date projectStartDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Tanggal Selesai Proyek")
    private Date projectEndDate;


}
