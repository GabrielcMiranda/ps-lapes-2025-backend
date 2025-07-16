package lapes.cesupa.ps_backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lapes.cesupa.ps_backend.model.User;


@Repository
public interface UserRepository extends JpaRepository<User,UUID> {
    
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String login);
}
