package gpl.karina.purchase.model;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "resource_temp")
@SQLDelete(sql = "UPDATE resource_temp SET is_deleted = TRUE WHERE id=?")
@SQLRestriction("is_deleted IS FALSE")
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

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
