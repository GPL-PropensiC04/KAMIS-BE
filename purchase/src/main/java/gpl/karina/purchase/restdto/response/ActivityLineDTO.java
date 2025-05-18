package gpl.karina.purchase.restdto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLineDTO {
    private String period;
    private Long count;
}
