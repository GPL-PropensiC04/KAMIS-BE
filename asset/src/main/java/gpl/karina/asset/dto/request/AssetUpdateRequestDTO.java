package gpl.karina.asset.dto.request;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetUpdateRequestDTO {
    private String nama;
    private String jenisAset;
    private String status;
    private String deskripsi;
}
