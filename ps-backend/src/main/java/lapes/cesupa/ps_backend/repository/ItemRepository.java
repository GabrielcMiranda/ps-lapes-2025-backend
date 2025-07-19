package lapes.cesupa.ps_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import lapes.cesupa.ps_backend.model.Item;


@Repository
public interface ItemRepository extends JpaRepository<Item,Long>, JpaSpecificationExecutor<Item> {
    
    Optional<Item> findByName(String name);
}
