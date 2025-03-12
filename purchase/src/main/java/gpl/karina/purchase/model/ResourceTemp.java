package gpl.karina.purchase.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class ResourceTemp {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(nullable = false, name = "Id Barang")
    private Long resourceId;
    @Column(nullable = false, name = "Nama Barang")
    private String resourceName;
    @Column(nullable = false, name = "Jumlah Barang")
    private Integer resourceTotal;
    @Column(nullable = false, name = "Harga Barang")
    private Integer resourcePrice;
}
