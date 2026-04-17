package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  UPGRADE ENGINE — Street Racer
 * ══════════════════════════════════════════════════════════════
 *
 *  Catégories :
 *  ENGINE, TRANSMISSION, SUSPENSION, BRAKES, WEIGHT
 *    → niveaux 0 (Stock) → 1 (Sport) → 2 (Racing)
 *
 *  TIRES → sous-types :
 *    TIRES_STREET      : pneus de rue (léger gain grip + longévité)
 *    TIRES_SPORT       : semi-slick (plus de grip, durabilité correcte)
 *    TIRES_SUPERSOFT   : Racing Très Tendre  (grip max, usure ultra-rapide)
 *    TIRES_SOFT        : Racing Tendre       (très bon grip, usure rapide)
 *    TIRES_MEDIUM      : Racing Normal       (bon grip, usure modérée)
 *    TIRES_HARD        : Racing Dur          (grip correct, longue durée)
 *    TIRES_SUPERHARD   : Racing Très Dur     (grip moindre, longévité max)
 *
 *  Prix des pneus Racing min 20 000 CR (Normal).
 *  Prix changement auto-shop basé sur tireModel.
 * ══════════════════════════════════════════════════════════════
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/upgrade")
public class UpgradeController {

    @Autowired private UserRepository       userRepository;
    @Autowired private CarRepository        carRepository;
    @Autowired private CarUpgradeRepository upgradeRepository;

    // ── GET : état upgrades d'une voiture ──────────────────
    @GetMapping("/car/{carId}")
    public ResponseEntity<?> getUpgrades(@PathVariable Long carId) {
        CarUpgrade upgrades = upgradeRepository.findByCarId(carId)
                .orElseGet(() -> {
                    Car car = carRepository.findById(carId).orElseThrow();
                    CarUpgrade fresh = new CarUpgrade();
                    fresh.setCar(car);
                    return upgradeRepository.save(fresh);
                });
        return ResponseEntity.ok(upgrades);
    }

    // ── POST : acheter un upgrade standard (ENGINE, TRANS, SUSP, BRAKES, WEIGHT) ─
    @PostMapping("/buy")
    public ResponseEntity<?> buyUpgrade(@RequestBody UpgradeRequest req) {
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new RuntimeException("Joueur introuvable"));
        Car car = carRepository.findById(req.carId())
                .orElseThrow(() -> new RuntimeException("Voiture introuvable"));

        if (car.getGarage() == null || !car.getGarage().getId().equals(user.getGarage().getId())) {
            return ResponseEntity.badRequest().body("Cette voiture ne t'appartient pas !");
        }

        UpgradeType type;
        try {
            type = UpgradeType.valueOf(req.upgradeType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Type d'upgrade invalide : " + req.upgradeType());
        }

        // Les pneus ont leur propre endpoint
        if (type == UpgradeType.TIRES) {
            return ResponseEntity.badRequest().body("Utilise /api/upgrade/tires pour les pneus.");
        }

        CarUpgrade upgrades = upgradeRepository.findByCarId(car.getId())
                .orElseGet(() -> { CarUpgrade u = new CarUpgrade(); u.setCar(car); return upgradeRepository.save(u); });

        int currentLevel = getCurrentLevel(upgrades, type);
        if (currentLevel >= 2) {
            return ResponseEntity.badRequest().body(upgradeTypeName(type) + " déjà au niveau Racing !");
        }
        int nextLevel = currentLevel + 1;
        long cost = computeCost(car, type, nextLevel);
        if (user.getCredits() < cost) {
            return ResponseEntity.badRequest().body("Pas assez de crédits ! Il te faut " + cost + " CR.");
        }

        UpgradeResult result = new UpgradeResult();
        result.setPowerBefore(car.getPower()); result.setWeightBefore(car.getWeight());
        result.setGripBefore(car.getGripModifier()); result.setTireBefore(car.getTireType());

        applyUpgrade(car, upgrades, type, nextLevel);
        setCurrentLevel(upgrades, type, nextLevel);
        user.setCredits(user.getCredits() - cost);
        userRepository.save(user); carRepository.save(car); upgradeRepository.save(upgrades);

        result.setSuccess(true); result.setMessage(buildMessage(type, nextLevel));
        result.setCostPaid(cost); result.setNewBalance(user.getCredits());
        result.setPowerAfter(car.getPower()); result.setWeightAfter(car.getWeight());
        result.setGripAfter(round2(car.getGripModifier())); result.setTireAfter(car.getTireType());
        result.setEngineLevel(upgrades.getEngineLevel());
        result.setTransmissionLevel(upgrades.getTransmissionLevel());
        result.setSuspensionLevel(upgrades.getSuspensionLevel());
        result.setBrakesLevel(upgrades.getBrakesLevel());
        result.setWeightLevel(upgrades.getWeightLevel());
        result.setTiresLevel(upgrades.getTiresLevel());
        return ResponseEntity.ok(result);
    }

    // ── POST : acheter des pneus (choix du modèle) ────────
    @PostMapping("/tires/buy")
    public ResponseEntity<?> buyTires(@RequestBody TireBuyRequest req) {
        User user = userRepository.findById(req.userId()).orElseThrow();
        Car  car  = carRepository.findById(req.carId()).orElseThrow();
        if (car.getGarage() == null || !car.getGarage().getId().equals(user.getGarage().getId())) {
            return ResponseEntity.badRequest().body("Cette voiture ne t'appartient pas !");
        }

        TireSpec spec = TireSpec.fromModel(req.tireModel());
        if (spec == null) return ResponseEntity.badRequest().body("Modèle de pneu inconnu : " + req.tireModel());

        long cost = tireBuyCost(car, spec);
        if (user.getCredits() < cost) {
            return ResponseEntity.badRequest().body("Pas assez de crédits ! Il te faut " + cost + " CR.");
        }

        // Sauvegarde avant
        double gripBefore       = car.getGripModifier();
        String tireBefore       = car.getTireType();
        String tireModelBefore  = car.getTireModel(); // modèle AVANT changement

        // Calcul du grip de base (sans le bonus des pneus actuels)
        double oldTireBonus = gripBonusOfCurrent(tireModelBefore);
        double gripBase     = gripBefore - oldTireBonus; // grip mécanique pur

        System.out.println("[TIRES-BUY] gripBefore=" + gripBefore + " oldTireBonus=" + oldTireBonus + " gripBase=" + gripBase + " newBonus=" + spec.gripBonus());

        // Applique le nouveau pneu
        car.setTireType(spec.category());
        car.setTireModel(spec.model());
        car.setGripModifier(round2(gripBase + spec.gripBonus()));

        System.out.println("[TIRES-BUY] gripAfter=" + car.getGripModifier());
        car.setTireWear(100.0); // pneus neufs

        CarUpgrade upgrades = upgradeRepository.findByCarId(car.getId())
                .orElseGet(() -> { CarUpgrade u = new CarUpgrade(); u.setCar(car); return upgradeRepository.save(u); });
        upgrades.setTiresLevel(spec.upgradeLevel());

        user.setCredits(user.getCredits() - cost);
        userRepository.save(user); carRepository.save(car); upgradeRepository.save(upgrades);

        UpgradeResult result = new UpgradeResult();
        result.setSuccess(true);
        result.setMessage("Pneus " + spec.label() + " installés !");
        result.setCostPaid(cost); result.setNewBalance(user.getCredits());
        result.setGripBefore(gripBefore); result.setGripAfter(round2(car.getGripModifier()));
        result.setTireBefore(tireBefore); result.setTireAfter(car.getTireType());
        result.setPowerBefore(car.getPower()); result.setPowerAfter(car.getPower());
        result.setWeightBefore(car.getWeight()); result.setWeightAfter(car.getWeight());
        result.setEngineLevel(upgrades.getEngineLevel());
        result.setTransmissionLevel(upgrades.getTransmissionLevel());
        result.setSuspensionLevel(upgrades.getSuspensionLevel());
        result.setBrakesLevel(upgrades.getBrakesLevel());
        result.setWeightLevel(upgrades.getWeightLevel());
        result.setTiresLevel(upgrades.getTiresLevel());
        return ResponseEntity.ok(result);
    }

    // ── GET : catalogue pneus avec prix pour une voiture ──
    @GetMapping("/tires/catalog/{carId}")
    public ResponseEntity<?> getTiresCatalog(@PathVariable Long carId) {
        Car car = carRepository.findById(carId).orElseThrow();
        var list = java.util.Arrays.stream(TireSpec.values())
                .map(s -> new TireCatalogEntry(
                    s.model(), s.label(), s.category(), s.gripBonus(),
                    s.wearRateMultiplier(), s.upgradeLevel(),
                    tireBuyCost(car, s),
                    tireChangeCost(car, s),
                    s.description()
                )).toList();
        return ResponseEntity.ok(list);
    }

    // ══════════════════════════════════════════════════════
    //  TIRE SPECS CATALOGUE
    // ══════════════════════════════════════════════════════

    public enum TireSpec {
        // ── Rue (3 composés) ──────────────────────────────
        STREET_SOFT ("Street_Soft",      "Rue Tendre",    "Street",  0.03, 1.1,   0, "Pneus route tendres. Meilleur grip par temps froid.",          1200,  400),
        STREET_MED  ("Street",           "Rue Normal",    "Street",  0.00, 1.0,   0, "Pneus de serie. Bon compromis confort/durabilite.",            1500,  500),
        STREET_HARD ("Street_Hard",      "Rue Dur",       "Street", -0.02, 0.8,   0, "Pneus route durs. Longue duree de vie.",                       1000,  350),
        // ── Sport (3 composés) ────────────────────────────
        SPORT_SOFT  ("Sport_Soft",       "Sport Tendre",  "Sport",   0.12, 1.0,   1, "Semi-slick tendre. Tres bon grip, duree correcte.",            7000, 1500),
        SPORT       ("Sport",            "Sport Normal",  "Sport",   0.08, 0.85,  1, "Semi-slick. Bon grip, durabilite correcte.",                   6000, 1200),
        SPORT_HARD  ("Sport_Hard",       "Sport Dur",     "Sport",   0.04, 0.7,   1, "Semi-slick dur. Durabilite superieure, grip modere.",          5000,  900),
        // ── Racing (5 composés) ───────────────────────────
        RACING_SS   ("Racing_SuperSoft", "Racing SSoft",  "Racing",  0.28, 3.5,   2, "Tres tendre. Grip maximum, s use a chaque virage.",           30000, 8000),
        RACING_SOFT ("Racing_Soft",      "Racing Soft",   "Racing",  0.22, 2.5,   2, "Tendre. Excellent grip, duree de vie courte.",                25000, 6500),
        RACING_MED  ("Racing_Medium",    "Racing Medium", "Racing",  0.16, 1.6,   2, "Normal. Bon equilibre performance/duree de vie.",             20000, 5000),
        RACING_HARD ("Racing_Hard",      "Racing Hard",   "Racing",  0.10, 1.0,   2, "Dur. Durabilite elevee, grip legerement inferieur.",          22000, 5500),
        RACING_SH   ("Racing_SuperHard", "Racing SHard",  "Racing",  0.04, 0.7,   2, "Tres dur. Longevite max, grip reduit.",                       18000, 4500);

        private final String model, label, category, description;
        private final double gripBonus, wearRateMultiplier;
        private final int upgradeLevel;
        private final long baseBuyCost, baseChangeCost;

        TireSpec(String model, String label, String category, double gripBonus,
                 double wearRateMultiplier, int upgradeLevel, String description,
                 long baseBuyCost, long baseChangeCost) {
            this.model = model; this.label = label; this.category = category;
            this.gripBonus = gripBonus; this.wearRateMultiplier = wearRateMultiplier;
            this.upgradeLevel = upgradeLevel; this.description = description;
            this.baseBuyCost = baseBuyCost; this.baseChangeCost = baseChangeCost;
        }

        public String model()               { return model; }
        public String label()               { return label; }
        public String category()            { return category; }
        public double gripBonus()           { return gripBonus; }
        public double wearRateMultiplier()  { return wearRateMultiplier; }
        public int    upgradeLevel()        { return upgradeLevel; }
        public String description()         { return description; }
        public long   baseBuyCost()         { return baseBuyCost; }
        public long   baseChangeCost()      { return baseChangeCost; }

        public static TireSpec fromModel(String model) {
            for (TireSpec s : values()) if (s.model.equalsIgnoreCase(model)) return s;
            return null;
        }
    }

    /** Grip bonus actuellement appliqué par le type de pneu actuel */
    /** Retourne le gripBonus du modèle de pneu ACTUELLEMENT monté (avant changement) */
    private double gripBonusOfCurrent(String currentTireModel) {
        if (currentTireModel == null) return 0;
        TireSpec cur = TireSpec.fromModel(currentTireModel);
        return cur != null ? cur.gripBonus() : 0;
    }

    /** Coût d'achat d'un jeu de pneus selon la gamme de la voiture */
    private long tireBuyCost(Car car, TireSpec spec) {
        long carPrice = Math.max(car.getPrice(), 5000L);
        // Multiplicateur voiture pour pneus de rue/sport
        double carMult = carPrice < 15000 ? 1.0 : carPrice < 50000 ? 1.3 : carPrice < 120000 ? 1.6 : 2.0;
        if (spec.category().equals("Racing")) {
            // Les pneus racing ont leur propre tarif fixe, majoré par gamme voiture
            return Math.round(spec.baseBuyCost() * carMult);
        }
        return Math.round(spec.baseBuyCost() * carMult);
    }

    /** Coût changement pneus à l'auto-shop (remise à 100%) */
    public long tireChangeCost(Car car, TireSpec spec) {
        long carPrice = Math.max(car.getPrice(), 5000L);
        double carMult = carPrice < 15000 ? 1.0 : carPrice < 50000 ? 1.3 : carPrice < 120000 ? 1.6 : 2.0;
        return Math.max(500L, Math.round(spec.baseChangeCost() * carMult));
    }

    // ══════════════════════════════════════════════════════
    //  UPGRADE FORMULAS (moteur, freins, etc.)
    // ══════════════════════════════════════════════════════

    private void applyUpgrade(Car car, CarUpgrade upg, UpgradeType type, int level) {
        switch (type) {
            case ENGINE -> {
                int baseP = car.getPower();
                double mult = (level == 1) ? 0.13 : 0.25;
                car.setPower(baseP + (int)(baseP * mult));
            }
            case TRANSMISSION -> {
                int baseP = car.getPower();
                double multP = (level == 1) ? 0.06 : 0.12;
                car.setPower(baseP + (int)(baseP * multP));
                double gripGain = (level == 1) ? 0.02 : 0.04;
                car.setGripModifier(round2(car.getGripModifier() + gripGain));
            }
            case SUSPENSION -> {
                double gripGain = (level == 1) ? 0.06 : 0.14;
                car.setGripModifier(round2(car.getGripModifier() + gripGain));
            }
            case BRAKES -> {
                double gripGain = (level == 1) ? 0.04 : 0.10;
                car.setGripModifier(round2(car.getGripModifier() + gripGain));
            }
            case WEIGHT -> {
                int baseW = car.getWeight();
                double reduction = (level == 1) ? 0.05 : 0.10;
                car.setWeight(baseW - (int)(baseW * reduction));
            }
            case TIRES -> {} // géré par /tires/buy
        }
    }

    private long computeCost(Car car, UpgradeType type, int level) {
        long carPrice = Math.max(car.getPrice(), 5000L);
        double tierMult;
        if      (carPrice < 15000)  tierMult = 1.2;
        else if (carPrice < 50000)  tierMult = 1.8;
        else if (carPrice < 120000) tierMult = 2.4;
        else                        tierMult = 3.5;
        long basePrice = Math.round(carPrice * tierMult);
        double baseRatio = switch (type) {
            case ENGINE       -> 0.16;
            case TRANSMISSION -> 0.12;
            case SUSPENSION   -> 0.11;
            case BRAKES       -> 0.10;
            case WEIGHT       -> 0.14;
            case TIRES        -> 0.09;
        };
        double levelMult = (level == 1) ? 1.0 : 2.8;
        return Math.max(1000L, Math.round(basePrice * baseRatio * levelMult));
    }

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

    private String buildMessage(UpgradeType type, int level) {
        String lvl = level == 1 ? "Sport" : "Racing";
        return upgradeTypeName(type) + " upgradé en " + lvl + " !";
    }

    private String upgradeTypeName(UpgradeType type) {
        return switch (type) {
            case ENGINE       -> "Moteur";
            case TRANSMISSION -> "Transmission";
            case SUSPENSION   -> "Suspension";
            case BRAKES       -> "Freins";
            case WEIGHT       -> "Poids";
            case TIRES        -> "Pneus";
        };
    }

    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }
    private double baseGrip(Car car, CarUpgrade upg) { return car.getGripModifier(); }

    // ── Records ────────────────────────────────────────────
    public record TireBuyRequest(long userId, long carId, String tireModel) {}
    public static class TireCatalogEntry {
        public final String model, label, category, description;
        public final double gripBonus, wearRateMultiplier;
        public final int    upgradeLevel;
        public final long   buyCost, changeCost;
        public TireCatalogEntry(String model, String label, String category,
                double gripBonus, double wearRateMultiplier, int upgradeLevel,
                long buyCost, long changeCost, String description) {
            this.model = model; this.label = label; this.category = category;
            this.gripBonus = gripBonus; this.wearRateMultiplier = wearRateMultiplier;
            this.upgradeLevel = upgradeLevel; this.buyCost = buyCost;
            this.changeCost = changeCost; this.description = description;
        }
    }
}
