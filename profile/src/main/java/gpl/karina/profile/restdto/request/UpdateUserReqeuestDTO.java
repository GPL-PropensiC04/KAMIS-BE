package gpl.karina.profile.restdto.request;

import lombok.Data;

@Data
public class UpdateUserReqeuestDTO {
    private String email;
    private String password;
    private String username;
}
