package com.example.garage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring comprend automatiquement qu'il doit chercher par le champ "username"
    Optional<User> findByUsername(String username);
}
