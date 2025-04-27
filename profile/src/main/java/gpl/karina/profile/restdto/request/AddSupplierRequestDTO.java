package gpl.karina.profile.restdto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AddSupplierRequestDTO {

    @NotNull(message = "Nama tidak boleh kosong")
    @NotBlank(message = "Nama tidak boleh kosong")
    private String nameSupplier;

    @NotNull(message = "Nomor Telepon tidak boleh kosong")
    private String noTelpSupplier;

    @NotNull(message = "Email tidak boleh kosong")
    private String emailSupplier;

    @NotNull(message = "Perusahaan tidak boleh kosong")
    private String companySupplier;

    @NotNull(message = "Alamat tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    @NotBlank(message = "Email tidak boleh kosong")
    private String emailSupplier;

    @NotNull(message = "Perusahaan tidak boleh kosong")
    @NotBlank(message = "Perusahaan tidak boleh kosong")
    private String companySupplier;

    @NotNull(message = "Alamat tidak boleh kosong")
    @NotBlank(message = "Alamat tidak boleh kosong")
    private String addressSupplier;

    // Optional: boleh kosong
    private List<Long> resourceIds;
}
