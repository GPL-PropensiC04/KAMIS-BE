package gpl.karina.asset.dto.request;

import lombok.*;
import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetUpdateRequestDTO {
    private String nama;
    private String jenisAset;
    private String status;
    private String deskripsi;
    private MultipartFile foto;
}
