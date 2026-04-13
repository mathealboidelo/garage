package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  UPGRADE ENGINE — Street Racer
 * ══════════════════════════════════════════════════════════════
 *
 *  6 CATÉGORIES × 3 NIVEAUX  (0=Stock → 1=Sport → 2=Racing)
 *
 *  ENGINE       : +power progressif selon base et aspiration
 *  TRANSMISSION : +power modéré + léger gain de grip
 *  SUSPENSION   : +gripModifier (tenue de route)
 *  BRAKES       : +gripModifier (freinage = sortie de virage)
 *  WEIGHT       : -weight (allègement carbone/titane)
 *  TIRES        : upgrade tireType + gripModifier
 *
 *  PRIX : scalé sur le prix d'achat de la voiture (~5-25%)
 * ══════════════════════════════════════════════════════════════
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/upgrade")
public class UpgradeController {

    @Autowired private UserRepository       userRepository;
    @Autowired private CarRepository        carRepository;
    @Autowired private CarUpgradeRepository upgradeRepository;

    // ── GET : récupérer l'état des upgrades d'une voiture ──
    @GetMapping("/car/{carId}")
    public ResponseEntity<?> getUpgrades(@PathVariable Long carId) {
        CarUpgrade upgrades = upgradeRepository.findByCarId(carId)
                .orElseGet(() -> {
                    // Crée un enregistrement vierge si pas encore upgradé
                    Car car = carRepository.findById(carId).orElseThrow();
                    CarUpgrade fresh = new CarUpgrade();
                    fresh.setCar(car);
                    return upgradeRepository.save(fresh);
                });
        return ResponseEntity.ok(upgrades);
    }

    // ── POST : acheter un upgrade ──────────────────────────
    @PostMapping("/buy")
    public ResponseEntity<?> buyUpgrade(@RequestBody UpgradeRequest req) {

        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new RuntimeException("Joueur introuvable"));
        Car car = carRepository.findById(req.carId())
                .orElseThrow(() -> new RuntimeException("Voiture introuvable"));

        // Vérifie que la voiture appartient au joueur
        if (car.getGarage() == null || !car.getGarage().getId().equals(user.getGarage().getId())) {
            return ResponseEntity.badRequest().body("Cette voiture ne t'appartient pas !");
        }

        CarUpgrade upgrades = upgradeRepository.findByCarId(car.getId())
                .orElseGet(() -> {
                    CarUpgrade fresh = new CarUpgrade();
                    fresh.setCar(car);
                    return upgradeRepository.save(fresh);
                });

        UpgradeType type;
        try {
            type = UpgradeType.valueOf(req.upgradeType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Type d'upgrade invalide : " + req.upgradeType());
        }

        // Niveau actuel de cette catégorie
        int currentLevel = getCurrentLevel(upgrades, type);
        if (currentLevel >= 2) {
            return ResponseEntity.badRequest().body("Upgrade déjà au niveau maximum (Racing) !");
        }

        int nextLevel = currentLevel + 1;
        long cost = computeCost(car, type, nextLevel);

        if (user.getCredits() < cost) {
            return ResponseEntity.badRequest()
                    .body("Pas assez de crédits ! Il te faut " + cost + " CR.");
        }

        // ── Sauvegarde stats AVANT ────────────────────────
        UpgradeResult result = new UpgradeResult();
        result.setPowerBefore(car.getPower());
        result.setWeightBefore(car.getWeight());
        result.setGripBefore(car.getGripModifier());
        result.setTireBefore(car.getTireType());

        // ── Application de l'upgrade ──────────────────────
        applyUpgrade(car, upgrades, type, nextLevel);
        setCurrentLevel(upgrades, type, nextLevel);

        // ── Transaction financière ────────────────────────
        user.setCredits(user.getCredits() - cost);
        userRepository.save(user);
        carRepository.save(car);
        upgradeRepository.save(upgrades);

        // ── Construction du résultat ──────────────────────
        result.setSuccess(true);
        result.setMessage(buildMessage(type, nextLevel));
        result.setCostPaid(cost);
        result.setNewBalance(user.getCredits());

        result.setPowerAfter(car.getPower());
        result.setWeightAfter(car.getWeight());
        result.setGripAfter(round2(car.getGripModifier()));
        result.setTireAfter(car.getTireType());

        result.setEngineLevel(upgrades.getEngineLevel());
        result.setTransmissionLevel(upgrades.getTransmissionLevel());
        result.setSuspensionLevel(upgrades.getSuspensionLevel());
        result.setBrakesLevel(upgrades.getBrakesLevel());
        result.setWeightLevelVal(upgrades.getWeightLevel());
        result.setTiresLevel(upgrades.getTiresLevel());

        return ResponseEntity.ok(result);
    }

    // ══════════════════════════════════════════════════════
    //  UPGRADE FORMULAS
    // ══════════════════════════════════════════════════════

    private void applyUpgrade(Car car, CarUpgrade upg, UpgradeType type, int level) {
        switch (type) {

            case ENGINE -> {
                // Sport L1: +10-15% power  |  Racing L2: +25-35% total
                double mult = (level == 1) ? 0.13 : 0.25;
                // Turbo gagne un peu plus
                if (car.getAspiration() == AspirationType.TURBO) mult += 0.04;
                int base = basePower(car, upg);
                car.setPower(base + (int)(base * mult));
            }

            case TRANSMISSION -> {
                // Gain modéré en puissance (meilleure transmission de couple) + grip léger
                double multP = (level == 1) ? 0.06 : 0.12;
                int base = basePower(car, upg);
                car.setPower(base + (int)(base * multP));
                double gripGain = (level == 1) ? 0.02 : 0.04;
                double baseGrip = baseGrip(car, upg);
                car.setGripModifier(round2(baseGrip + gripGain));
            }

            case SUSPENSION -> {
                double gripGain = (level == 1) ? 0.06 : 0.14;
                double baseGrip = baseGrip(car, upg);
                car.setGripModifier(round2(baseGrip + gripGain));
            }

            case BRAKES -> {
                // Freins → meilleure sortie de virage = grip en entrée/sortie
                double gripGain = (level == 1) ? 0.04 : 0.10;
                double baseGrip = baseGrip(car, upg);
                car.setGripModifier(round2(baseGrip + gripGain));
            }

            case WEIGHT -> {
                // Sport: -5%  |  Racing: -10% (carbone/titane)
                double reduction = (level == 1) ? 0.05 : 0.10;
                int baseW = baseWeight(car, upg);
                car.setWeight(baseW - (int)(baseW * reduction));
            }

            case TIRES -> {
                if (level == 1) {
                    // Sport → Semi-Slick
                    car.setTireType("Semi-Slick");
                    double baseGrip = baseGrip(car, upg);
                    car.setGripModifier(round2(baseGrip + 0.08));
                } else {
                    // Racing → Slick
                    car.setTireType("Slick");
                    double baseGrip = baseGrip(car, upg);
                    car.setGripModifier(round2(baseGrip + 0.18));
                }
            }
        }
    }

    // ── Récupère la puissance de base (niveau 0) ─────────
    private int basePower(Car car, CarUpgrade upg) {
        // On recalcule depuis le niveau 0 pour éviter l'empilement
        // On stocke la base dans les champs directement — ici approche simple :
        // on travaille sur la valeur courante sans re-cascader
        return car.getPower();
    }

    private double baseGrip(Car car, CarUpgrade upg) {
        return car.getGripModifier();
    }

    private int baseWeight(Car car, CarUpgrade upg) {
        return car.getWeight();
    }

    // ── Coût d'un upgrade ─────────────────────────────────
    private long computeCost(Car car, UpgradeType type, int level) {
        // Base = % du prix d'achat (min 500 CR)
        long basePrice = Math.max(car.getPrice(), 5000L);
        double ratio = switch (type) {
            case ENGINE       -> (level == 1) ? 0.12 : 0.22;
            case TRANSMISSION -> (level == 1) ? 0.08 : 0.15;
            case SUSPENSION   -> (level == 1) ? 0.07 : 0.13;
            case BRAKES       -> (level == 1) ? 0.06 : 0.11;
            case WEIGHT       -> (level == 1) ? 0.10 : 0.20;
            case TIRES        -> (level == 1) ? 0.05 : 0.10;
        };
        return Math.max(500L, Math.round(basePrice * ratio));
    }

    // ── Getters/Setters de niveau par type ────────────────
    private int getCurrentLevel(CarUpgrade upg, UpgradeType type) {
        return switch (type) {
            case ENGINE       -> upg.getEngineLevel();
            case TRANSMISSION -> upg.getTransmissionLevel();
            case SUSPENSION   -> upg.getSuspensionLevel();
            case BRAKES       -> upg.getBrakesLevel();
            case WEIGHT       -> upg.getWeightLevel();
            case TIRES        -> upg.getTiresLevel();
        };
    }

    private void setCurrentLevel(CarUpgrade upg, UpgradeType type, int level) {
        switch (type) {
            case ENGINE       -> upg.setEngineLevel(level);
            case TRANSMISSION -> upg.setTransmissionLevel(level);
            case SUSPENSION   -> upg.setSuspensionLevel(level);
            case BRAKES       -> upg.setBrakesLevel(level);
            case WEIGHT       -> upg.setWeightLevel(level);
            case TIRES        -> upg.setTiresLevel(level);
        }
    }

    // ── Message de confirmation ───────────────────────────
    private String buildMessage(UpgradeType type, int level) {
        String levelName = (level == 1) ? "Sport" : "Racing";
        String typeName  = switch (type) {
            case ENGINE       -> "Moteur";
            case TRANSMISSION -> "Transmission";
            case SUSPENSION   -> "Suspension";
            case BRAKES       -> "Freins";
            case WEIGHT       -> "Allègement";
            case TIRES        -> "Pneus";
        };
        return typeName + " upgradé en " + levelName + " !";
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
