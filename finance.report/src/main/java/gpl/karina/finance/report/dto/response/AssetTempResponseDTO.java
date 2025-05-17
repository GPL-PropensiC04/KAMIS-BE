package gpl.karina.finance.report.dto.response;

import lombok.Data;

@Data
public class AssetTempResponseDTO { //belum add attribut foto
    private long id;
    private String assetNameString;
    private String assetDescription;
    private String assetType;
    private Integer assetPrice;
    private String fotoContentType;
    private String fotoUrl;
}
