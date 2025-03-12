package gpl.karina.asset.dto.response;
import lombok.*;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponseDTO {
    private String id;
    private String nama;
    private String deskripsi;
    private Date tanggalPerolehan;
    private Float nilaiPerolehan;
    private String assetMaintenance;
    // private List<String> historiMaintenance;
}
