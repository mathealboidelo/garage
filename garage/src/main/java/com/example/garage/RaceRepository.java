package com.example.garage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RaceRepository extends JpaRepository<Race, Long> {
    Optional<Race> findByName(String name);
}
