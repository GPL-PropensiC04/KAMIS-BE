package gpl.karina.profile.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("OPERASIONAL")
@Table(name = "Operasional")
public class Operasional extends EndUser{
    
}
