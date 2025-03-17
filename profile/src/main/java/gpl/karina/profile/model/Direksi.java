package gpl.karina.profile.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("DIREKSI")
@Table(name = "Direksi")
public class Direksi extends EndUser{
    
}