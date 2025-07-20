package lapes.cesupa.ps_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lapes.cesupa.ps_backend.model.Item;
import lapes.cesupa.ps_backend.model.Review;
import lapes.cesupa.ps_backend.model.User;

public interface ReviewRepository extends JpaRepository<Review,Long>{

    boolean existsByCustomer(User customer);

    boolean existsByItem(Item item);
}
