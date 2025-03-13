package gpl.karina.purchase.restdto.response;

import lombok.Data;

@Data
public class AssetTempResponseDTO { //belum add attribut foto
    private String assetNameString;
    private String assetDescription;
    private String assetType;
    private Integer assetPrice;
    private String fotoContentType;
    private String fotoUrl;
}
