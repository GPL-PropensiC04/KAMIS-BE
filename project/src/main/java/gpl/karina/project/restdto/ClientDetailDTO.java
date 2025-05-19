package gpl.karina.project.restdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientDetailDTO {
    private String id;
    private String nameClient; // Corrected field name
}
