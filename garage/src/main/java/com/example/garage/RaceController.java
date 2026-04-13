package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RaceController {

    @Autowired private RaceRepository raceRepository;

    @GetMapping("/api/races")
    public List<Race> getAllRaces() { return raceRepository.findAll(); }

    @GetMapping("/api/races/{id}")
    public ResponseEntity<Race> getById(@PathVariable Long id) {
        return raceRepository.findById(id)
                .map(r -> ResponseEntity.ok().body(r))
                .orElse(ResponseEntity.notFound().build());
    }
}
