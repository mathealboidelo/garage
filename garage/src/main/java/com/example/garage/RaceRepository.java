package com.example.garage;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceRepository extends JpaRepository<Race, Long>{

	Optional<Race> findByName(String name);
}
