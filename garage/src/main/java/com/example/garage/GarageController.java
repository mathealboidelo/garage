package com.example.garage;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class GarageController {

    @GetMapping("/api/status") // L'adresse de ta page
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("game", "Apex Racing Manager");
        status.put("version", "0.1-Alpha");
        status.put("status", "Engine Running");
        status.put("message", "Bienvenue dans ton garage, pilote !");
        return status;
    }
}