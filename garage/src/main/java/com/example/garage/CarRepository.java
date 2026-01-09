package com.example.garage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long>{
	
	Optional<Car> findByName(String name);
	
	List<Car> findByGarageId(Long garageId);
}

