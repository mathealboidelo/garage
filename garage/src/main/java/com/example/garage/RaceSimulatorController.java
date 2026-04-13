package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * ══════════════════════════════════════════════════════════════
 *  RACE SIMULATION ENGINE v3 — Street Racer
 * ══════════════════════════════════════════════════════════════
 *
 *  NOUVEAUTÉS v3 :
 *  ─ GET /api/race/generate?opponentId=X&carId=Y&userId=Z
 *      → Génère une course aléatoire, calcule la difficulté et
 *        le multiplicateur de mise AVANT que le joueur confirme.
 *        Le frontend affiche ces infos puis appelle /api/race/run.
 *
 *  ─ Difficulté (1-10) basée sur :
 *      • Ratio puissance adversaire / joueur
 *      • Topologie de la course (virages pénalisent les voitures lourdes)
 *      • Usure pneus/huile du joueur
 *      • Type adversaire (boss/gang = +2, spécial = +3)
 *
 *  ─ Multiplicateur de mise :
 *      diff 1-3 → ×1.1   (facile)
 *      diff 4-5 → ×1.3   (moyen)
 *      diff 6-7 → ×1.6   (difficile)
 *      diff 8-9 → ×2.0   (très difficile)
 *      diff 10  → ×2.5   (extrême)
 *
 *  ─ Pari voiture autorisé même avec 1 seule voiture,
 *    mais si le joueur perd il se retrouve sans voiture
 *    (état "sans voiture" géré côté front).
 * ══════════════════════════════════════════════════════════════
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RaceSimulatorController {

    @Autowired private UserRepository         userRepo;
    @Autowired private RacersRepository       racersRepo;
    @Autowired private RaceRepository         raceRepo;
    @Autowired private CarRepository          carRepo;
    @Autowired private DealershipRepository   dealerRepo;
    @Autowired private UserProgressRepository progressRepo;

    private static final Random RNG = new Random();

    private static final double TIRE_WEAR_PER_RACE = 8.0;
    private static final double OIL_DECAY_PER_RACE = 5.0;

    private static final int REP_WIN_NORMAL  = 10;
    private static final int REP_WIN_GANG    = 20;
    private static final int REP_WIN_BOSS    = 50;
    private static final int REP_WIN_SPECIAL = 35;

    // ══════════════════════════════════════════════════════
    //  GÉNÉRATION DE COURSE ALÉATOIRE
    // ══════════════════════════════════════════════════════

    /**
     * Génère une course aléatoire pour un défi donné.
     * Le frontend doit appeler cet endpoint AVANT /api/race/run.
     * La raceId retournée est à passer dans RaceRequest.
     */
    @GetMapping("/api/race/generate")
    public ResponseEntity<GeneratedRace> generateRace(
            @RequestParam long opponentId,
            @RequestParam long carId,
            @RequestParam long userId) {

        Racers opponent = racersRepo.findById(opponentId).orElseThrow();
        Car    playerCar = carRepo.findById(carId).orElseThrow();

        // Sélectionne un circuit aléatoire parmi tous les circuits en base
        List<Race> allRaces = raceRepo.findAll()
                .stream().filter(r -> r != null && r.getName() != null).toList();
        if (allRaces.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Race race = allRaces.get(RNG.nextInt(allRaces.size()));

        GeneratedRace gen = new GeneratedRace();
        gen.setRaceId(race.getId());
        gen.setRaceName(race.getName());
        gen.setStraightLine(race.getStraigthLine());
        gen.setCorner(race.getCorner());

        // ── Calcul de difficulté ──────────────────────────
        int diff = computeDifficulty(playerCar, opponent, race);
        gen.setDifficulty(diff);
        gen.setDifficultyLabel(difficultyLabel(diff));
        gen.setBetMultiplier(betMultiplier(diff));
        gen.setDifficultyReason(difficultyReason(playerCar, opponent, race, diff));

        // ── Plafond de mise (Option C + D) ───────────────
        User user = userRepo.findById(userId).orElseThrow();
        long maxBet = computeMaxBet(user, opponent.getCar());
        gen.setMaxBet(maxBet);

        // Option D : message de refus si la mise dépasserait le seuil de l'adversaire
        long refusalThreshold = (long)(opponent.getCar().getPrice() * 0.3);
        if (refusalThreshold > 0 && maxBet < user.getCredits()) {
            gen.setOpponentRefusalMsg(buildRefusalMsg(opponent, refusalThreshold));
        }

        return ResponseEntity.ok(gen);
    }

    // ══════════════════════════════════════════════════════
    //  LANCEMENT DE COURSE
    // ══════════════════════════════════════════════════════

    @PostMapping("/api/race/run")
    public ResponseEntity<?> runRace(@RequestBody RaceRequest req) {

        User   user     = userRepo.findById(req.userId()).orElseThrow();
        Racers opponent = racersRepo.findById(req.opponentId()).orElseThrow();
        Race   race     = raceRepo.findById(req.raceId()).orElseThrow();

        // Validation crédits + plafond dynamique (seulement si pas de pari voiture)
        if (!req.betCar()) {
            if (user.getCredits() < req.bet()) {
                return ResponseEntity.badRequest().body("Pas assez de credits !");
            }
            long maxBet = computeMaxBet(user, opponent.getCar());
            if (req.bet() > maxBet) {
                return ResponseEntity.badRequest().body(
                    "Mise trop élevée ! Maximum autorisé : " + maxBet + " CR");
            }
        }
        if (user.getGarage() == null || user.getGarage().getCars().isEmpty()) {
            return ResponseEntity.badRequest().body("Aucune voiture dans le garage !");
        }

        // Voiture du joueur
        Car playerCar = user.getGarage().getCars().stream()
                .filter(c -> c.getId().equals(req.carId()))
                .findFirst()
                .orElse(user.getGarage().getCars().get(0));

        // Pari voiture — autorisé même avec 1 seule voiture (punitif)
        Car wageredCar = null;
        if (req.betCar()) {
            wageredCar = user.getGarage().getCars().stream()
                    .filter(c -> c.getId().equals(req.carId()))
                    .findFirst().orElse(null);
            if (wageredCar == null) {
                return ResponseEntity.badRequest().body("Voiture mise en jeu introuvable !");
            }
            // PAS de vérification "au moins 2 voitures" — c'est voulu, c'est punitif
        }

        // ── Score ─────────────────────────────────────────
        double sr = race.getStraigthLine() / 100.0;
        double cr = race.getCorner()       / 100.0;

        double playerScore   = computeScore(playerCar, sr, cr);
        double opponentScore = computeScore(opponent.getCar(), sr, cr);
        boolean won = playerScore > opponentScore;

        // ── Difficulté et multiplicateur ──────────────────
        int    diff        = computeDifficulty(playerCar, opponent, race);
        double multiplier  = betMultiplier(diff);

        // ── UserProgress ──────────────────────────────────
        UserProgress progress = progressRepo.findByUserId(user.getId()).orElseGet(() -> {
            UserProgress p = new UserProgress();
            p.setUser(user);
            return progressRepo.save(p);
        });

        // ── Usure ─────────────────────────────────────────
        double newTire = Math.max(0, playerCar.getTireWear() - TIRE_WEAR_PER_RACE);
        double newOil  = Math.max(0, playerCar.getOilQuality() - OIL_DECAY_PER_RACE);
        playerCar.setTireWear(newTire);
        playerCar.setOilQuality(newOil);
        playerCar.setRacesCount(playerCar.getRacesCount() + 1);

        // ── Transaction financière ─────────────────────────
        long creditsChange = 0;
        long actualGain    = 0;
        boolean lostCar    = false;
        String wonCarName  = null;

        if (req.betCar()) {
            if (won) {
                Car opCar = opponent.getCar();
                Car prize = cloneCar(opCar, user.getGarage());
                carRepo.save(prize);
                wonCarName = opCar.getName();
            } else {
                // Perd sa voiture — même si c'est la dernière
                wageredCar.setGarage(null);
                carRepo.save(wageredCar);
                lostCar = true;
            }
        } else {
            // Gain = mise × multiplicateur de difficulté
            actualGain    = (long) Math.floor(req.bet() * multiplier);
            creditsChange = won ? actualGain : -req.bet();
            user.setCredits(user.getCredits() + creditsChange);
        }

        // ── Réputation ────────────────────────────────────
        int repEarned = 0;
        boolean gangMemberDefeated = false;
        boolean bossDefeated       = false;
        boolean specialCarUnlocked = false;
        String  specialCarName     = null;
        long    specialCarId       = 0;

        if (won) {
            user.setWins(user.getWins() + 1);
            if (opponent.isSpecial()) {
                repEarned = REP_WIN_SPECIAL;
                if (opponent.getSpecialCarForSale() != null) {
                    Car sc = opponent.getSpecialCarForSale();
                    List<Dealership> dealers = dealerRepo.findAll();
                    if (!dealers.isEmpty()) {
                        Dealership target = dealers.get(dealers.size() - 1);
                        sc.setDealership(target); sc.setGarage(null);
                        carRepo.save(sc);
                        specialCarUnlocked = true;
                        specialCarName = sc.getName();
                        specialCarId   = sc.getId();
                    }
                }
            } else if (opponent.isBoss()) {
                repEarned = REP_WIN_BOSS; bossDefeated = true;
                progress.addDefeated(opponent.getId());
            } else if (opponent.isGangMember()) {
                repEarned = REP_WIN_GANG; gangMemberDefeated = true;
                progress.addDefeated(opponent.getId());
            } else {
                repEarned = REP_WIN_NORMAL;
            }
            user.setReputation(user.getReputation() + repEarned);
            user.setLevel(Math.min(1 + user.getReputation() / 500, 99));
        }

        carRepo.save(playerCar);
        userRepo.save(user);
        progressRepo.save(progress);

        // ── Résultat ──────────────────────────────────────
        RaceResult result = new RaceResult();
        result.setPlayerWon(won);
        result.setPlayerName(user.getUsername());
        result.setOpponentName(opponent.getDisplayName());
        result.setRaceName(race.getName());
        result.setStraightLine(race.getStraigthLine());
        result.setCorner(race.getCorner());
        result.setPlayerScore(r2(playerScore));
        result.setOpponentScore(r2(opponentScore));
        result.setCreditsEarned(creditsChange);
        result.setActualGain(actualGain);
        result.setNewBalance(user.getCredits());
        result.setReputationEarned(repEarned);
        result.setNewReputation(user.getReputation());
        result.setNewTireWear(r2(newTire));
        result.setNewOilQuality(r2(newOil));
        result.setGangMemberDefeated(gangMemberDefeated);
        result.setBossDefeated(bossDefeated);
        result.setCarWager(req.betCar());
        result.setWonCarName(wonCarName);
        result.setLostCar(lostCar);
        result.setSpecialCarUnlocked(specialCarUnlocked);
        result.setSpecialCarName(specialCarName);
        result.setSpecialCarId(specialCarId);
        result.setDifficulty(diff);
        result.setDifficultyLabel(difficultyLabel(diff));
        result.setBetMultiplier(multiplier);
        result.setResultMessage(buildMsg(won, opponent.getDisplayName(), race.getName(), playerScore, opponentScore, multiplier, actualGain, req.betCar()));

        return ResponseEntity.ok(result);
    }

    // ══════════════════════════════════════════════════════
    //  CALCUL DE DIFFICULTÉ
    // ══════════════════════════════════════════════════════

    /**
     * Score de difficulté de 1 à 10.
     *
     * Facteurs :
     *  1. Rapport de score estimé adversaire/joueur        → 0-4 pts
     *  2. Topologie défavorable au joueur (pneus usés+virages, etc.) → 0-2 pts
     *  3. Type adversaire (boss, spécial)                 → 0-2 pts
     *  4. Usure critique                                  → 0-2 pts
     */
    private int computeDifficulty(Car playerCar, Racers opponent, Race race) {
        double sr = race.getStraigthLine() / 100.0;
        double cr = race.getCorner()       / 100.0;

        // Scores estimés (sans bruit)
        double pScore = computeScoreDeterministic(playerCar, sr, cr);
        double oScore = computeScoreDeterministic(opponent.getCar(), sr, cr);

        // 1. Ratio adversaire vs joueur (0-4 pts)
        double ratio = oScore / Math.max(pScore, 0.001);
        int ratioPts;
        if      (ratio < 0.7)  ratioPts = 0;  // joueur très supérieur
        else if (ratio < 0.85) ratioPts = 1;
        else if (ratio < 1.0)  ratioPts = 2;
        else if (ratio < 1.2)  ratioPts = 3;
        else                    ratioPts = 4;  // adversaire bien supérieur

        // 2. Topologie défavorable : circuit virage avec mauvais pneus (0-2 pts)
        int topoPts = 0;
        if (cr > 0.5 && playerCar.getTireWear() < 40) topoPts += 2;
        else if (cr > 0.5 && playerCar.getTireWear() < 70) topoPts += 1;
        else if (sr > 0.7 && playerCar.getOilQuality() < 40) topoPts += 1;

        // 3. Type adversaire (0-2 pts)
        int typePts = 0;
        if (opponent.isSpecial()) typePts = 3;
        else if (opponent.isBoss()) typePts = 2;
        else if (opponent.isGangMember()) typePts = 1;

        // 4. Usure critique du joueur (0-2 pts)
        int usurePts = 0;
        if (playerCar.getTireWear() < 20 || playerCar.getOilQuality() < 20) usurePts = 2;
        else if (playerCar.getTireWear() < 40 || playerCar.getOilQuality() < 40) usurePts = 1;

        int total = ratioPts + topoPts + typePts + usurePts;
        return Math.max(1, Math.min(10, total));
    }

    private String difficultyLabel(int diff) {
        if (diff <= 2)  return "Facile";
        if (diff <= 4)  return "Moyen";
        if (diff <= 6)  return "Difficile";
        if (diff <= 8)  return "Très difficile";
        return "Extrême";
    }

    private double betMultiplier(int diff) {
        if (diff <= 2)  return 1.1;
        if (diff <= 4)  return 1.3;
        if (diff <= 6)  return 1.6;
        if (diff <= 8)  return 2.0;
        return 2.5;
    }

    private String difficultyReason(Car playerCar, Racers opponent, Race race, int diff) {
        StringBuilder sb = new StringBuilder();
        double sr = race.getStraigthLine() / 100.0;
        double cr = race.getCorner() / 100.0;

        if (cr > 0.5 && playerCar.getTireWear() < 40)
            sb.append("Circuit technique avec pneus usés. ");
        if (sr > 0.7 && playerCar.getOilQuality() < 40)
            sb.append("Circuit rapide avec huile dégradée. ");
        if (opponent.isBoss())
            sb.append("Boss de gang. ");
        if (opponent.isSpecial())
            sb.append("Adversaire spécial. ");

        double pScore = computeScoreDeterministic(playerCar, sr, cr);
        double oScore = computeScoreDeterministic(opponent.getCar(), sr, cr);
        if (oScore > pScore * 1.2)
            sb.append("Adversaire nettement plus rapide sur ce circuit.");
        else if (oScore < pScore * 0.8)
            sb.append("Tu es favori sur ce circuit.");

        return sb.length() > 0 ? sb.toString().trim() : "Course équilibrée.";
    }

    // ══════════════════════════════════════════════════════
    //  FORMULE DE SIMULATION
    // ══════════════════════════════════════════════════════

    /** Avec bruit gaussien (utilisé pour la vraie course) */
    private double computeScore(Car car, double sr, double cr) {
        double tireMulti = 0.80 + (car.getTireWear() / 100.0) * 0.20;
        double oilMulti  = 0.85 + (car.getOilQuality() / 100.0) * 0.15;
        double aspBonus  = "TURBO".equalsIgnoreCase(car.getAspiration().name()) ? 1.15 : 1.0;
        double straight  = (car.getPower() * oilMulti / car.getWeight()) * aspBonus * 100.0;
        double corner    = car.getGripModifier() * tireMulti * tireCoeff(car.getTireType()) * (1000.0 / car.getWeight()) * 100.0;
        double base      = sr * straight + cr * corner;
        double noise     = clamp(1.0 + RNG.nextGaussian() * 0.08, 0.88, 1.12);
        return base * noise;
    }

    /** Sans bruit (utilisé pour estimer la difficulté) */
    private double computeScoreDeterministic(Car car, double sr, double cr) {
        double tireMulti = 0.80 + (car.getTireWear() / 100.0) * 0.20;
        double oilMulti  = 0.85 + (car.getOilQuality() / 100.0) * 0.15;
        double aspBonus  = "TURBO".equalsIgnoreCase(car.getAspiration().name()) ? 1.15 : 1.0;
        double straight  = (car.getPower() * oilMulti / car.getWeight()) * aspBonus * 100.0;
        double corner    = car.getGripModifier() * tireMulti * tireCoeff(car.getTireType()) * (1000.0 / car.getWeight()) * 100.0;
        return sr * straight + cr * corner;
    }

    private double tireCoeff(String t) {
        if (t == null) return 1.0;
        return switch (t.toLowerCase()) {
            case "slick"      -> 1.20;
            case "semi-slick" -> 1.10;
            case "rain"       -> 0.85;
            default           -> 1.00;
        };
    }

    private Car cloneCar(Car src, Garage dest) {
        Car c = new Car();
        c.setName(src.getName()); c.setPower(src.getPower()); c.setWeight(src.getWeight());
        c.setGripModifier(src.getGripModifier()); c.setAspiration(src.getAspiration());
        c.setTireType(src.getTireType()); c.setPrice(src.getPrice());
        c.setGarage(dest); c.setDealership(null);
        c.setTireWear(100.0); c.setOilQuality(100.0);
        return c;
    }

    private double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }
    private double r2(double v) { return Math.round(v * 100.0) / 100.0; }

    // ══════════════════════════════════════════════════════
    //  PLAFOND DE MISE DYNAMIQUE (Option C + D)
    // ══════════════════════════════════════════════════════

    /**
     * Calcule le plafond de mise maximal pour une course donnée.
     *
     * Option C — double plafond :
     *   • Prix voiture adverse × 0.6  (mise cohérente avec l'enjeu)
     *   • 1000 + réputation × 15      (grandit avec la progression)
     *
     * Le minimum des deux s'applique, puis le minimum avec les crédits du joueur.
     */
    private long computeMaxBet(User user, Car opponentCar) {
        long repCap = 1000L + (long)(user.getReputation() * 15);

        // Si la voiture adversaire a un prix > 0 (voiture de concessionnaire),
        // on applique aussi le plafond basé sur la valeur de la voiture.
        // Si price == 0 (voiture adversaire sans prix), on utilise uniquement repCap.
        long cap;
        if (opponentCar.getPrice() > 0) {
            long carCap = (long)(opponentCar.getPrice() * 0.6);
            cap = Math.min(carCap, repCap);
        } else {
            // Voiture adversaire sans prix → plafond basé sur réputation uniquement
            cap = repCap;
        }

        cap = Math.max(cap, 100);
        return Math.min(cap, user.getCredits());
    }

    /**
     * Option D — message narratif de refus si la mise dépasse
     * ce que l'adversaire "peut se permettre" (prix voiture × 0.3).
     */
    private String buildRefusalMsg(Racers opponent, long threshold) {
        String name = opponent.getName();
        String car  = opponent.getCar().getName();
        String[] templates = {
            name + " secoue la tete. Sa " + car + " ne vaut pas autant. Il accepte " + threshold + " CR max.",
            name + " ricane : mise max " + threshold + " CR avec moi, pas un de plus.",
            "La " + car + " de " + name + " vaut pas ta mise. Plafond fixe a " + threshold + " CR.",
            name + " croise les bras. Mise max " + threshold + " CR, c\'est tout.",
        };
        return templates[RNG.nextInt(templates.length)];
    }

    private String buildMsg(boolean won, String opp, String race,
                            double ps, double os, double mult, long gain, boolean betCar) {
        if (betCar) {
            return won
                ? String.format("Victoire sur %s ! Tu remportes la voiture de %s !", race, opp)
                : String.format("Défaite sur %s. Tu as perdu ta voiture...", race);
        }
        String d = String.format("%.1f", Math.abs(ps - os));
        return won
            ? String.format("Victoire sur %s ! ×%.1f → +%d CR (score %.1f vs %.1f)", race, mult, gain, ps, os)
            : String.format("Défaite sur %s. Score %.1f vs %.1f (écart %s)", race, ps, os, d);
    }
}