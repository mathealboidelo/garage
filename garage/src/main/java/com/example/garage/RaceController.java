package com.example.garage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RaceController {
	
	@Autowired
	private RaceRepository raceRepository;

	@GetMapping("/api/races/{id}")
	public ResponseEntity<Race> getAllRacesById(@PathVariable Long id) {
	    return raceRepository.findById(id)
	            .map(dealer -> ResponseEntity.ok().body(dealer))
	            .orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/api/races")
	public List<Race> getAllRaces() {
		return raceRepository.findAll();
	}
}
