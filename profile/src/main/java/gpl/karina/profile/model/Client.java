package gpl.karina.profile.model;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "Client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "Nama", nullable = false, unique = true)
    private String nameClient;

    @Column(name = "Nomor Telepon", nullable = false, unique = true)
    private String noTelpClient;

    @Column(name = "Email", nullable = false, unique = true)
    private String emailClient;

    @Column(name = "Tipe", nullable = false) // Tipe 0 = perorangan, 1 = perusahaan
    private boolean typeClient;

    @Column(name = "Perusahaan", nullable = true)
    private String companyClient;

    @Column(name = "Alamat", nullable = false)
    private String addressClient;

    //TODO: attribute untuk hubungin client sama Distribusi & Penjualan (financial history ambil dari sini juga)

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Created Date", updatable = false, nullable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Updated Date", nullable = false)
    private Date updatedDate;
}
