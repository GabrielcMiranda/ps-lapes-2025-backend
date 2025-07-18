package lapes.cesupa.ps_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lapes.cesupa.ps_backend.model.Order;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
