package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RacersController {

    @Autowired private RacersRepository racersRepository;

    @GetMapping("/api/racers")
    public List<Racers> getAllRacers() { return racersRepository.findAll(); }
}
