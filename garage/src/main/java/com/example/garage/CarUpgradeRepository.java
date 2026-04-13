package com.example.garage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarUpgradeRepository extends JpaRepository<CarUpgrade, Long> {
    Optional<CarUpgrade> findByCarId(Long carId);
}
