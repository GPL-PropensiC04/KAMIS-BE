package gpl.karina.profile.restdto.request;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UpdateSupplierRequestDTO {
    @NotNull(message = "ID Supplier wajib diisi")
    private UUID id;

    @NotNull(message = "Address Supplier wajib diisi")
    @NotBlank(message = "Address Supplier tidak boleh kosong")
    private String addressSupplier;

    @NotNull(message = "No Telp Supplier wajib diisi")
    @NotBlank(message = "No telpon Supplier tidak boleh kosong")
    private String noTelpSupplier;

    @Email(message = "Format email tidak valid")
    @NotBlank(message = "Email Supplier tidak boleh kosong")
    private String emailSupplier;

    @NotNull(message = "Name Supplier wajib diisi")
    @NotBlank(message = "Nama Supplier (PIC) tidak boleh kosong")
    private String nameSupplier;

    private List<Long> resourceIds;
}
