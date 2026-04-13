package com.example.garage;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ParkingRepository extends JpaRepository<Parking, Long> {}
