package gpl.karina.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_asset_usage")
public class ProjectAssetUsage {
    @Id
    @Column(nullable = false, unique = true, name = "plat_nomor")
    private String platNomor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Column(nullable = false, name = "asset_use_cost")
    private Integer assetUseCost;

    @Column(nullable = false, name = "asset_fuel_cost")
    private Integer assetFuelCost;
}