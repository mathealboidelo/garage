package com.example.garage;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private CarRepository  carRepository;

    // ── DTOs (pas de relations JPA = pas de boucle JSON) ──
    public record CarDTO(
        Long id, String name, int power, int weight,
        double gripModifier, String aspiration, String tireType,
        long price, double tireWear, double oilQuality, int racesCount
    ) {}

    public record GarageDTO(Long id, List<CarDTO> cars) {}

    public record UserDTO(
        Long id, String username, long credits,
        int level, int reputation, int wins,
        GarageDTO garage
    ) {}

    private CarDTO toCarDTO(Car c) {
        if (c == null) return null;
        return new CarDTO(
            c.getId(), c.getName(), c.getPower(), c.getWeight(),
            c.getGripModifier(),
            c.getAspiration() != null ? c.getAspiration().name() : "",
            c.getTireType(), c.getPrice(),
            c.getTireWear(), c.getOilQuality(), c.getRacesCount()
        );
    }

    private GarageDTO toGarageDTO(Garage g) {
        if (g == null) return new GarageDTO(null, List.of());
        List<CarDTO> cars = g.getCars() == null ? List.of() :
            g.getCars().stream()
             .filter(c -> c != null && c.getId() != null)
             .map(this::toCarDTO)
             .collect(Collectors.toList());
        return new GarageDTO(g.getId(), cars);
    }

    private UserDTO toDTO(User u) {
        return new UserDTO(
            u.getId(), u.getUsername(), u.getCredits(),
            u.getLevel(), u.getReputation(), u.getWins(),
            toGarageDTO(u.getGarage())
        );
    }

    @GetMapping("/api/users")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u != null && u.getId() != null && u.getUsername() != null)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(toDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/users")
    public UserDTO addUser(@RequestBody User user) {
        Garage g = new Garage();
        user.setGarage(g);
        user.setLevel(1);
        user.setCredits(15000);
        return toDTO(userRepository.save(user));
    }

    @DeleteMapping("/api/deleteuser/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PostMapping("/api/buy")
    public ResponseEntity<String> addCarToGarage(@RequestBody BuyRequest request) {
        User user     = userRepository.findById(request.userId()).orElseThrow();
        Car  original = carRepository.findById(request.carId()).orElseThrow();

        if (user.getCredits() < original.getPrice()) {
            return ResponseEntity.badRequest().body("Pas assez d'argent !");
        }

        Car copy = new Car();
        copy.setName(original.getName());
        copy.setPower(original.getPower());
        copy.setWeight(original.getWeight());
        copy.setGripModifier(original.getGripModifier());
        copy.setAspiration(original.getAspiration());
        copy.setTireType(original.getTireType());
        copy.setPrice(original.getPrice());
        copy.setGarage(user.getGarage());
        copy.setDealership(null);
        copy.setTireWear(100.0);
        copy.setOilQuality(100.0);

        user.setCredits(user.getCredits() - original.getPrice());
        carRepository.save(copy);
        userRepository.save(user);

        return ResponseEntity.ok("Achat réussi !");
    }

    @PostMapping("/api/cheatmoney/{id}")
    public ResponseEntity<String> cheatMoney(@PathVariable Long id) {
        User u = userRepository.findById(id).orElseThrow();
        u.setCredits(u.getCredits() + 10000);
        userRepository.save(u);
        return ResponseEntity.ok("Argent ajouté !");
    }

    @PostMapping("/api/cheatrep/{id}")
    public ResponseEntity<String> cheatRep(@PathVariable Long id) {
        User u = userRepository.findById(id).orElseThrow();
        u.setReputation(u.getReputation() + 100);
        int newLevel = 1 + u.getReputation() / 500;
        u.setLevel(Math.min(newLevel, 99));
        userRepository.save(u);
        return ResponseEntity.ok("Réputation +100 !");
    }
}