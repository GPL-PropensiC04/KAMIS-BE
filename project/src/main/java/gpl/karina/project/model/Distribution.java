package gpl.karina.project.model;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("DISTRIBUSI")
@Table(name = "distribusi")
public class Distribution extends Project {
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "id_aset_yang_digunakan")
    List<ProjectAssetUsage> projectUseAsset;

    @Column(name = "alamat_pengambilan")
    private String projectPickupAddress;

    @Column(name = "jumlah_phl_yang_dipekerjakan")
    private Integer projectPHLCount;

    @Column(name = "biaya_phl")
    private Long projectPHLPay;

    @Column(name = "total_pengeluaran")
    private Long projectTotalPengeluaran;
    
}
