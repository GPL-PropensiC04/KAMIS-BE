package gpl.karina.asset.dto.request;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequestDTO {
    
    @NotBlank(message = "Plat nomor asset tidak boleh kosong")
    private String platNomor;
    
    @NotBlank(message = "Deskripsi pekerjaan tidak boleh kosong")
    private String deskripsiPekerjaan;
    
    @NotNull(message = "Biaya tidak boleh kosong")
    @Positive(message = "Biaya harus bernilai positif")
    private Float biaya;
    
    @NotNull(message = "Tanggal mulai tidak boleh kosong")
    private Date tanggalMulaiMaintenance;
}