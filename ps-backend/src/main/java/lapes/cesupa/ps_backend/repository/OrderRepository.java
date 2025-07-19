package lapes.cesupa.ps_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lapes.cesupa.ps_backend.model.Order;

public interface OrderRepository extends JpaRepository<Order,Long>, JpaSpecificationExecutor<Order> {

}
