package gpl.karina.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_resource_usage")
public class ProjectResourceUsage {
    @Id
    @Column(nullable = false, unique = true, name = "resource_id")
    private String resourceId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Column(nullable = false, name = "resource_price")
    private Integer sellPrice;

    @Column(nullable = false, name = "resource_stock_used")
    private Integer quantityUsed;
}