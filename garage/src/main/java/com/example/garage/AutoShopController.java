package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Vendeur de pièces auto :
 *  - Changement de pneus (remet tireWear à 100)
 *  - Vidange d'huile (remet oilQuality à 100)
 *  - Vente d'une voiture (50% du prix d'achat)
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/autoshop")
public class AutoShopController {

    @Autowired private UserRepository userRepo;
    @Autowired private CarRepository  carRepo;

    private static final double TIRE_CHANGE_RATIO  = 0.04;  // 4%  du prix voiture
    private static final double OIL_CHANGE_RATIO   = 0.02;  // 2%  du prix voiture
    private static final long   MIN_SERVICE_COST    = 500L;

    @PostMapping("/tires/{carId}/user/{userId}")
    public ResponseEntity<?> changeTires(@PathVariable Long carId, @PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        Car  car  = carRepo.findById(carId).orElseThrow();
        if (!ownscar(user, car)) return ResponseEntity.badRequest().body("Voiture introuvable dans ton garage.");

        long cost = Math.max(MIN_SERVICE_COST, Math.round(car.getPrice() * TIRE_CHANGE_RATIO));
        if (user.getCredits() < cost) return ResponseEntity.badRequest().body("Pas assez de credits ! (" + cost + " CR)");

        user.setCredits(user.getCredits() - cost);
        car.setTireWear(100.0);
        carRepo.save(car);
        userRepo.save(user);

        return ResponseEntity.ok(new ServiceResult("Pneus changés !", cost, user.getCredits(), car.getTireWear(), car.getOilQuality()));
    }

    @PostMapping("/oil/{carId}/user/{userId}")
    public ResponseEntity<?> changeOil(@PathVariable Long carId, @PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        Car  car  = carRepo.findById(carId).orElseThrow();
        if (!ownscar(user, car)) return ResponseEntity.badRequest().body("Voiture introuvable dans ton garage.");

        long cost = Math.max(MIN_SERVICE_COST, Math.round(car.getPrice() * OIL_CHANGE_RATIO));
        if (user.getCredits() < cost) return ResponseEntity.badRequest().body("Pas assez de credits ! (" + cost + " CR)");

        user.setCredits(user.getCredits() - cost);
        car.setOilQuality(100.0);
        carRepo.save(car);
        userRepo.save(user);

        return ResponseEntity.ok(new ServiceResult("Vidange effectuée !", cost, user.getCredits(), car.getTireWear(), car.getOilQuality()));
    }

    @PostMapping("/sell/{carId}/user/{userId}")
    public ResponseEntity<?> sellCar(@PathVariable Long carId, @PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        Car  car  = carRepo.findById(carId).orElseThrow();
        if (!ownscar(user, car)) return ResponseEntity.badRequest().body("Voiture introuvable.");
        if (user.getGarage().getCars().size() <= 1) return ResponseEntity.badRequest().body("Tu ne peux pas vendre ta seule voiture !");

        long salePrice = car.getPrice() / 2;
        user.setCredits(user.getCredits() + salePrice);
        car.setGarage(null);
        carRepo.save(car);
        userRepo.save(user);

        return ResponseEntity.ok(new ServiceResult(car.getName() + " vendue !", salePrice, user.getCredits(), 0, 0));
    }

    @GetMapping("/costs/{carId}")
    public ResponseEntity<?> getCosts(@PathVariable Long carId) {
        Car car = carRepo.findById(carId).orElseThrow();
        long tires = Math.max(MIN_SERVICE_COST, Math.round(car.getPrice() * TIRE_CHANGE_RATIO));
        long oil   = Math.max(MIN_SERVICE_COST, Math.round(car.getPrice() * OIL_CHANGE_RATIO));
        long sell  = car.getPrice() / 2;
        return ResponseEntity.ok(new CostInfo(tires, oil, sell));
    }

    private boolean ownscar(User user, Car car) {
        return car.getGarage() != null && car.getGarage().getId().equals(user.getGarage().getId());
    }

    public record ServiceResult(String message, long cost, long newBalance, double newTireWear, double newOilQuality) {}
    public record CostInfo(long tireCost, long oilCost, long saleValue) {}
}
