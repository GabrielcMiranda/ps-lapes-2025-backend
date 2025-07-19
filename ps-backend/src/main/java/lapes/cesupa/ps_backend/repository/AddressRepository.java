package lapes.cesupa.ps_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lapes.cesupa.ps_backend.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long>{

}
