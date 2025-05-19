package gpl.karina.profile.restdto.request;

import lombok.Data;

@Data
public class UpdateClientRequestDTO {
    private String nameClient;
    private String noTelpClient;
    private String emailClient;
    private String addressClient;
}
