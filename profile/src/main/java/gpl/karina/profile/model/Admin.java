package gpl.karina.profile.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("ADMIN")
@Table(name = "Admin")
public class Admin extends EndUser{ 
    
}
