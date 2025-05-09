package gpl.karina.project.model;

import java.util.UUID;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;
    
    // Reference to external Asset service entity
    @Column(nullable = false, name = "plat_nomor")
    private String platNomor;
    
    @Column(nullable = false, name = "tipe_aset")
    private String tipeAset;

    // Reference to parent Project 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    // Context-specific data (usage costs in this project)
    @Column(nullable = false, name = "asset_use_cost")
    private Integer assetUseCost;

    @Column(nullable = false, name = "asset_fuel_cost")
    private Integer assetFuelCost;
}