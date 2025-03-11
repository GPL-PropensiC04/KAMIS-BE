package gpl.karina.profile.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndUserResponseDTO {
    private String email;
    private String password;
    private String name;
}
