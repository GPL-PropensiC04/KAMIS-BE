package gpl.karina.profile.restdto.request;

import lombok.Data;

@Data
public class AddUserReqeuestDTO {
    private String email;
    private String password;
    private String name;
}
