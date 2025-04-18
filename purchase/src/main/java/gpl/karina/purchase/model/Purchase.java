package gpl.karina.purchase.model;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "purchase")
public class Purchase {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(nullable = false, name = "Supplier Pembelian")
    private String purchaseSupplier;
    @Column(nullable = false, name = "Tipe Barang")
    private boolean purchaseType; // Value 0 = Aset, Value 1 = Resource
    @Column(nullable = false, name = "Status Pembelian")
    private String purchaseStatus;
    @Column(nullable = false, name = "Harga Pembelian")
    private Integer purchasePrice;
    @Column(nullable = false, name = "Catatan Pembelian")
    private String purchaseNote;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Tanggal Pengajuan", updatable = false, nullable = false)
    private Date purchaseSubmissionDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Tanggal Pembaruan", nullable = false)
    private Date purchaseUpdateDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "purchase_id")
    List<ResourceTemp> purchaseResource;

    @Column(name = "Asset Dibeli", nullable = true)
    private Long purchaseAsset;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Tanggal Pembayaran")
    private Date purchasePaymentDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "purchase_id")
    List<LogPurchase> purchaseLogs;
}
