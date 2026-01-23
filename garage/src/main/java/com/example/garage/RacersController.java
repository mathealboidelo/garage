package com.example.garage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RacersController {
	
	@Autowired
	private RacersRepository racersRepository;
	
	@GetMapping("/api/racers")
	public List<Racers> getAllRacers() {
		return racersRepository.findAll();
	}
}
