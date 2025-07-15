package lapes.cesupa.ps_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lapes.cesupa.ps_backend.model.Category;
import lapes.cesupa.ps_backend.model.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<User> findByName(String name);
}
