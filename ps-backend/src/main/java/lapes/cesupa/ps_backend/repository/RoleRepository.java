package lapes.cesupa.ps_backend.repository;

import java.util.UUID;

import javax.management.relation.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lapes.cesupa.ps_backend.model.User;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    
}
