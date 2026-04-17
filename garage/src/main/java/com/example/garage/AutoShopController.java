package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/autoshop")
public class AutoShopController {

    @Autowired private UserRepository userRepo;
    @Autowired private CarRepository  carRepo;

    private static final double OIL_CHANGE_RATIO = 0.02;
    private static final long   MIN_SERVICE_COST  = 500L;

    @PostMapping("/tires/{carId}/user/{userId}")
    public ResponseEntity<?> changeTires(@PathVariable Long carId, @PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        Car  car  = carRepo.findById(carId).orElseThrow();
        if (!owns(user, car)) return ResponseEntity.badRequest().body("Voiture introuvable dans ton garage.");

        long cost = tireChangeCost(car);
        if (user.getCredits() < cost) return ResponseEntity.badRequest().body("Pas assez de credits ! (" + cost + " CR)");
        user.setCredits(user.getCredits() - cost);
        car.setTireWear(100.0);
        carRepo.save(car); userRepo.save(user);
        return ResponseEntity.ok(new ServiceResult("Pneus changes !", cost, user.getCredits(), car.getTireWear(), car.getOilQuality()));
    }

    @PostMapping("/oil/{carId}/user/{userId}")
    public ResponseEntity<?> changeOil(@PathVariable Long carId, @PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        Car  car  = carRepo.findById(carId).orElseThrow();
        if (!owns(user, car)) return ResponseEntity.badRequest().body("Voiture introuvable dans ton garage.");

        long cost = Math.max(MIN_SERVICE_COST, Math.round(car.getPrice() * OIL_CHANGE_RATIO));
        if (user.getCredits() < cost) return ResponseEntity.badRequest().body("Pas assez de credits ! (" + cost + " CR)");
        user.setCredits(user.getCredits() - cost);
        car.setOilQuality(100.0);
        carRepo.save(car); userRepo.save(user);
        return ResponseEntity.ok(new ServiceResult("Vidange effectuee !", cost, user.getCredits(), car.getTireWear(), car.getOilQuality()));
    }

    @PostMapping("/sell/{carId}/user/{userId}")
    public ResponseEntity<?> sellCar(@PathVariable Long carId, @PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        Car  car  = carRepo.findById(carId).orElseThrow();
        if (!owns(user, car)) return ResponseEntity.badRequest().body("Voiture introuvable.");
        if (user.getGarage().getCars().size() <= 1) return ResponseEntity.badRequest().body("Tu ne peux pas vendre ta seule voiture !");
        long salePrice = car.getPrice() / 2;
        user.setCredits(user.getCredits() + salePrice);
        car.setGarage(null);
        carRepo.save(car); userRepo.save(user);
        return ResponseEntity.ok(new ServiceResult(car.getName() + " vendue !", salePrice, user.getCredits(), 0, 0));
    }

    @GetMapping("/costs/{carId}")
    public ResponseEntity<?> getCosts(@PathVariable Long carId) {
        Car car = carRepo.findById(carId).orElseThrow();
        long tires = tireChangeCost(car);
        long oil   = Math.max(MIN_SERVICE_COST, Math.round(car.getPrice() * OIL_CHANGE_RATIO));
        long sell  = car.getPrice() / 2;
        String tireLabel = car.getTireModel() != null ? car.getTireModel() : "Street";
        return ResponseEntity.ok(new CostInfo(tires, oil, sell, tireLabel));
    }

    /** Calcule le coût de remplacement des pneus selon le modèle monté */
    private long tireChangeCost(Car car) {
        String model = car.getTireModel() != null ? car.getTireModel().toLowerCase() : "street";
        long carPrice = Math.max(car.getPrice(), 5000L);
        double carMult = carPrice < 15000 ? 1.0 : carPrice < 50000 ? 1.3 : carPrice < 120000 ? 1.6 : 2.0;
        long baseChange = switch (model) {
            case "racing_supersoft" -> 8000L;
            case "racing_soft"      -> 6500L;
            case "racing_medium"    -> 5000L;
            case "racing_hard"      -> 5500L;
            case "racing_superhard" -> 4500L;
            case "sport_soft"       -> 1500L;
            case "sport"            -> 1200L;
            case "sport_hard"       -> 900L;
            case "street_soft"      -> 400L;
            case "street_hard"      -> 350L;
            default                 -> 500L;  // street normal
        };
        return Math.max(MIN_SERVICE_COST, Math.round(baseChange * carMult));
    }

    private boolean owns(User user, Car car) {
        return car.getGarage() != null && car.getGarage().getId().equals(user.getGarage().getId());
    }

    public record ServiceResult(String message, long cost, long newBalance, double newTireWear, double newOilQuality) {}
    public record CostInfo(long tireCost, long oilCost, long saleValue, String currentTireLabel) {}
}
