package gpl.karina.project.model;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "log_project")
public class LogProject {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, name = "username")
    private String username;

    @Column(nullable = false, name = "Aksi")
    private String action;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Waktu dan Tanggal", updatable = false, nullable = false)
    private Date actionDate;
}
