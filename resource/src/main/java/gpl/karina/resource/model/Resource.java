package gpl.karina.resource.model;

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
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, name = "Nama Barang")
    private String resourceName;
    @Column(nullable = false, name = "Deskripsi Barang")
    private String resourceDescription;
    @Column(nullable = false, name = "Stok Barang")
    private Integer resourceStock;
    @Column(nullable = false, name = "Harga Barang")
    private Integer resourcePrice;
}
