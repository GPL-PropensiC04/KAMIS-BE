package gpl.karina.purchase.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data; 
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class AssetTemp { //TODO: Belum add attribute foto
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "Nama Aset")
    private String assetName;
    @Column(nullable = false, name = "Deskripsi Aset")
    private String assetDescription;
    @Column(nullable = false, name = "Jenis Aset")
    private String assetType;
    
    @Column(nullable = false, name = "Harga Aset")
    private Integer assetPrice;
}
