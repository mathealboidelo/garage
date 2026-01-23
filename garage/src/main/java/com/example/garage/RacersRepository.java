package com.example.garage;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RacersRepository extends JpaRepository<Racers, Long>{
	
	Optional<Racers> findByName(String name);
}
