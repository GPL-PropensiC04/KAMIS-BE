package gpl.karina.profile.restdto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateClientRequestDTO {
    @NotBlank(message = "Nama klien tidak boleh kosong")
    private String nameClient;

    @NotBlank(message = "Nomor telepon klien tidak boleh kosong")
    private String noTelpClient;

    @NotBlank(message = "Email klien tidak boleh kosong")
    private String emailClient;

    @NotBlank(message = "Alamat klien tidak boleh kosong")
    private String addressClient;
}
