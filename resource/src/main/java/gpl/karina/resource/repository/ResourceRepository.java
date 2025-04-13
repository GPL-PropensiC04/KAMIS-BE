package gpl.karina.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gpl.karina.resource.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long>{
    Resource findByResourceName(String resourceName);
}
