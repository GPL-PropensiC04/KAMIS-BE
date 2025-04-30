package gpl.karina.project.restdto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetUsageDTO {
    private String tipeAset;
    private String platNomor;
    private Integer assetUseCost;    
    private Integer assetFuelCost;
}
