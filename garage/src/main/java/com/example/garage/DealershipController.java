package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class DealershipController {

    @Autowired private DealershipRepository dealershipRepository;

    @GetMapping("/api/dealership")
    public List<Dealership> getAllDealerships() {
        return dealershipRepository.findAll();
    }

    @GetMapping("/api/dealership/{id}")
    public ResponseEntity<Dealership> getById(@PathVariable Long id) {
        return dealershipRepository.findById(id)
                .map(d -> ResponseEntity.ok().body(d))
                .orElse(ResponseEntity.notFound().build());
    }
}
