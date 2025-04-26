package gpl.karina.profile.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "Nama", nullable = false, unique = true)
    private String nameSupplier;

    @Column(name = "Nomor Telepon", nullable = false, unique = true)
    private String noTelpSupplier;

    @Column(name = "Email", nullable = false, unique = true)
    private String emailSupplier;

    @Column(name = "Perusahaan", nullable = true)
    private String companySupplier;

    @Column(name = "Alamat", nullable = false)
    private String addressSupplier;
    
    // Menyimpan ID dari Asset (dari Asset Service)
    @ElementCollection
    @CollectionTable(name = "supplier_assets", joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "asset_id")
    private List<String> assetIds = new ArrayList<>();

    // Menyimpan ID dari Resource (dari Resource Service)
    @ElementCollection
    @CollectionTable(name = "supplier_resources", joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "resource_id")
    private List<Long> resourceIds = new ArrayList<>();

    // Menyimpan ID dari Purchase (dari Purchase Service)
    @ElementCollection
    @CollectionTable(name = "supplier_purchases", joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "purchase_id")
    private List<String> purchaseIds = new ArrayList<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Created Date", updatable = false, nullable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Updated Date", nullable = false)
    private Date updatedDate;
}
