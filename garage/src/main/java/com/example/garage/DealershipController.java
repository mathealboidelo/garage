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
public class DealershipController {
	
	@Autowired
	private DealershipRepository dealershipRepository;
	
	@GetMapping("/api/dealership/{id}")
	public ResponseEntity<Dealership> getDealershipById(@PathVariable Long id) {
	    return dealershipRepository.findById(id)
	            .map(dealer -> ResponseEntity.ok().body(dealer))
	            .orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/api/dealership")
	public List<Dealership> getAllDealership() {
		return dealershipRepository.findAll();
	}

}
