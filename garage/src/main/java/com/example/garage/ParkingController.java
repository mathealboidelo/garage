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
public class ParkingController {
	
	@Autowired
	private ParkingRepository parkingRepository;
	
	@GetMapping("/api/parkings")
	public List<Parking> getAllParkings() {
		return parkingRepository.findAll();
	}
	
	@GetMapping("/api/parkings/{id}")
	public ResponseEntity<Parking> getParkingById(@PathVariable Long id) {
	    return parkingRepository.findById(id)
	            .map(dealer -> ResponseEntity.ok().body(dealer))
	            .orElse(ResponseEntity.notFound().build());
	}

}
