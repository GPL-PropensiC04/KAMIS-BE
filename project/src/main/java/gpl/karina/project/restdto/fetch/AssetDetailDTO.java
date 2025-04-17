package gpl.karina.project.restdto.fetch;

import lombok.Data;

@Data
public class AssetDetailDTO {
    private String platNomor;
    private String nama;
    private String jenisAset;
    private String status;
    private Integer nilaiPerolehan;
}
