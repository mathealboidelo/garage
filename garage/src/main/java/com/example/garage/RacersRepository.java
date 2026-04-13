package com.example.garage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface RacersRepository extends JpaRepository<Racers, Long> {
    Optional<Racers> findByName(String name);
}
