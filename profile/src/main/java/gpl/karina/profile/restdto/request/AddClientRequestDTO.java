package gpl.karina.profile.restdto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddClientRequestDTO {
    @NotNull(message = "Nama tidak boleh kosong")
    private String nameClient;
    @NotNull(message = "Nomor Telepon tidak boleh kosong")
    private String noTelpClient;
    @NotNull(message = "Email tidak boleh kosong")
    private String emailClient;
    @NotNull(message = "Tipe Klien tidak boleh kosong")
    private boolean typeClient;
    
    private String companyClient;
    private String addressClient;
}
