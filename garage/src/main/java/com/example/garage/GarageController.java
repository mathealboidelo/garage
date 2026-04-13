package com.example.garage;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class GarageController {

    @GetMapping("/api/status")
    public Map<String, Object> getStatus() {
        return Map.of(
            "game",    "Street Racer",
            "version", "1.5",
            "status",  "Engine Running"
        );
    }
}
