package gpl.karina.profile.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("FINANCE")
@Table(name = "Finance")
public class Finance extends EndUser {
    
}