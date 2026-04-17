package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/src")
public class SrcController {

    @Autowired private UserRepository      userRepo;
    @Autowired private SrcTeamRepository   teamRepo;
    @Autowired private SrcSeasonRepository seasonRepo;

    private static final int    TOTAL_RACES   = 10;
    private static final int    TOTAL_DRIVERS = 20;
    private static final int    LAPS          = 3;
    private static final double NOISE         = 0.08;
    // Usure pneus/huile par tour (%)
    private static final double TIRE_PER_LAP  = 12.0;
    private static final double OIL_PER_LAP   = 8.0;
    private static final Random RNG           = new Random();

    private static final List<SrcCircuit> JGTC_CIRCUITS = List.of(
        new SrcCircuit("Suzuka Circuit",               35, 65),
        new SrcCircuit("Fuji Speedway",                55, 45),
        new SrcCircuit("Twin Ring Motegi",             40, 60),
        new SrcCircuit("Autopolis",                    30, 70),
        new SrcCircuit("Okayama International Circuit",32, 68),
        new SrcCircuit("Sportsland SUGO",              28, 72),
        new SrcCircuit("Sepang International Circuit", 50, 50),
        new SrcCircuit("Chang International Circuit",  52, 48),
        new SrcCircuit("Buriram Circuit",              50, 50),
        new SrcCircuit("Nürburgring GP",               42, 58)
    );

    // ── 1. INVITATIONS ────────────────────────────────────

    @GetMapping("/invitations/{userId}")
    public ResponseEntity<List<TeamInvitation>> getInvitations(@PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        if (user.getReputation() < 300)
            return ResponseEntity.ok(List.of());
        if (seasonRepo.findByUserIdAndFinishedFalse(userId).isPresent())
            return ResponseEntity.ok(List.of());

        List<SrcTeam> allTeams = teamRepo.findAll().stream()
                .sorted(Comparator.comparingInt(SrcTeam::getRank)).toList();
        if (allTeams.isEmpty()) return ResponseEntity.ok(List.of());

        int rep = user.getReputation();
        int playerTier;
        if      (rep >= 2000) playerTier = 1;
        else if (rep >= 1500) playerTier = 2;
        else if (rep >= 1000) playerTier = 3;
        else if (rep >= 750)  playerTier = 4;
        else if (rep >= 600)  playerTier = 5;
        else if (rep >= 500)  playerTier = 6;
        else if (rep >= 450)  playerTier = 7;
        else if (rep >= 400)  playerTier = 8;
        else if (rep >= 350)  playerTier = 9;
        else                  playerTier = 10;

        final int tier = playerTier;
        List<SrcTeam> selected = allTeams.stream()
                .filter(t -> t.getRank() >= Math.max(1, tier - 1)
                          && t.getRank() <= Math.min(10, tier + 1))
                .toList();
        if (selected.isEmpty()) selected = List.of(allTeams.get(allTeams.size() - 1));

        return ResponseEntity.ok(selected.stream()
                .map(t -> toInvitation(t)).toList());
    }

    // ── 2. REJOINDRE ──────────────────────────────────────

    @PostMapping("/join")
    public ResponseEntity<?> joinTeam(@RequestBody JoinRequest req) {
        User user    = userRepo.findById(req.userId()).orElseThrow();
        SrcTeam team = teamRepo.findById(req.teamId()).orElseThrow();
        if (user.getReputation() < team.getReputationRequired())
            return ResponseEntity.badRequest().body("Réputation insuffisante !");
        if (seasonRepo.findByUserIdAndFinishedFalse(req.userId()).isPresent())
            return ResponseEntity.badRequest().body("Tu es déjà en championnat !");

        SrcSeason season = new SrcSeason();
        season.setUser(user);
        season.setTeam(team);
        seasonRepo.save(season);
        return ResponseEntity.ok(toSeasonState(season));
    }

    // ── 3. ÉTAT SAISON ────────────────────────────────────

    @GetMapping("/season/{userId}")
    public ResponseEntity<?> getSeason(@PathVariable Long userId) {
        return ResponseEntity.ok(
            seasonRepo.findByUserIdAndFinishedFalse(userId)
                      .map(this::toSeasonState).orElse(null)
        );
    }

    // ── 4. UPGRADE VOITURE ────────────────────────────────

    @GetMapping("/upgrades/{userId}")
    public ResponseEntity<?> getUpgradeOptions(@PathVariable Long userId) {
        SrcSeason season = seasonRepo.findByUserIdAndFinishedFalse(userId)
                .orElse(null);
        if (season == null) return ResponseEntity.badRequest().body("Pas de saison active.");
        SrcTeam team = season.getTeam();

        List<UpgradeOption> options = new ArrayList<>();
        // Moteur (par palier de 20 CH, max +100)
        if (team.getEngineBonus() < 100) {
            long cost = 50000L + team.getEngineBonus() * 8000L;
            options.add(new UpgradeOption("engine",
                "Moteur +20 CH",
                "Passe de " + team.effectivePower() + " à " + (team.effectivePower() + 20) + " CH",
                cost, team.getBudget() >= cost));
        }
        // Grip (par palier de +0.02, max +0.20 = gripBonus 20)
        if (team.getGripBonus() < 20) {
            long cost = 40000L + team.getGripBonus() * 5000L;
            options.add(new UpgradeOption("grip",
                "Aérodynamique +grip",
                String.format("Grip %.2f → %.2f", team.effectiveGrip(), team.effectiveGrip() + 0.02),
                cost, team.getBudget() >= cost));
        }
        // Réduction de poids (–10 kg par palier, max -80 kg = weightBonus 8)
        if (team.getWeightBonus() < 8) {
            long cost = 35000L + team.getWeightBonus() * 6000L;
            options.add(new UpgradeOption("weight",
                "Allègement -10 kg",
                "Passe de " + team.getCarWeight() + " à " + (team.getCarWeight() - team.getWeightBonus() * 10 - 10) + " kg",
                cost, team.getBudget() >= cost));
        }
        return ResponseEntity.ok(new UpgradeMenu(
            team.getName(), team.getTeamColor(), team.getBudget(),
            team.effectivePower(), team.effectiveGrip(),
            team.getCarWeight() - team.getWeightBonus() * 10,
            options
        ));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> applyUpgrade(@RequestBody UpgradeRequest req) {
        SrcSeason season = seasonRepo.findByUserIdAndFinishedFalse(req.userId())
                .orElseThrow(() -> new RuntimeException("Pas de saison active."));
        SrcTeam team = season.getTeam();

        return switch (req.type()) {
            case "engine" -> {
                if (team.getEngineBonus() >= 100)
                    yield ResponseEntity.badRequest().body("Moteur déjà au max.");
                long cost = 50000L + team.getEngineBonus() * 8000L;
                if (team.getBudget() < cost)
                    yield ResponseEntity.badRequest().body("Budget insuffisant.");
                team.setEngineBonus(team.getEngineBonus() + 20);
                team.setBudget(team.getBudget() - cost);
                teamRepo.save(team);
                yield ResponseEntity.ok("Moteur amélioré ! +" + team.effectivePower() + " CH");
            }
            case "grip" -> {
                if (team.getGripBonus() >= 20)
                    yield ResponseEntity.badRequest().body("Grip déjà au max.");
                long cost = 40000L + team.getGripBonus() * 5000L;
                if (team.getBudget() < cost)
                    yield ResponseEntity.badRequest().body("Budget insuffisant.");
                team.setGripBonus(team.getGripBonus() + 1);
                team.setBudget(team.getBudget() - cost);
                teamRepo.save(team);
                yield ResponseEntity.ok(String.format("Aéro améliorée ! Grip %.2f", team.effectiveGrip()));
            }
            case "weight" -> {
                if (team.getWeightBonus() >= 8)
                    yield ResponseEntity.badRequest().body("Poids déjà au minimum.");
                long cost = 35000L + team.getWeightBonus() * 6000L;
                if (team.getBudget() < cost)
                    yield ResponseEntity.badRequest().body("Budget insuffisant.");
                team.setWeightBonus(team.getWeightBonus() + 1);
                team.setBudget(team.getBudget() - cost);
                teamRepo.save(team);
                yield ResponseEntity.ok("Allègement effectué !");
            }
            default -> ResponseEntity.badRequest().body("Type d'upgrade inconnu.");
        };
    }

    // ── 5. COURSE ─────────────────────────────────────────

    @PostMapping("/race/run")
    public ResponseEntity<?> runSrcRace(@RequestBody SrcRaceRequest req) {
        User      user   = userRepo.findById(req.userId()).orElseThrow();
        SrcSeason season = seasonRepo.findByUserIdAndFinishedFalse(req.userId())
                .orElseThrow(() -> new RuntimeException("Pas de saison active"));
        SrcTeam team = season.getTeam();

        SrcCircuit chosen = JGTC_CIRCUITS.get(RNG.nextInt(JGTC_CIRCUITS.size()));
        double sr = chosen.straight() / 100.0;
        double cr = chosen.corner()   / 100.0;

        // ── Simulation sur 3 tours avec dégradation ───────
        // Chaque tour : pneus -TIRE_PER_LAP%, huile -OIL_PER_LAP%
        double tireWear = 100.0;
        double oilQuality = 100.0;
        double totalPlayerScore = 0;

        List<Double> lapScores = new ArrayList<>();
        for (int lap = 0; lap < LAPS; lap++) {
            double lapScore = computeTeamCarScoreWithWear(team, sr, cr, tireWear, oilQuality);
            lapScores.add(lapScore);
            totalPlayerScore += lapScore;
            tireWear   = Math.max(0, tireWear   - TIRE_PER_LAP);
            oilQuality = Math.max(0, oilQuality - OIL_PER_LAP);
        }
        double playerScore = totalPlayerScore / LAPS;
        double finalTireWear   = tireWear;
        double finalOilQuality = oilQuality;

        // ── 19 adversaires (score moyen sur 3 tours aussi) ─
        List<SrcTeam> allTeams = teamRepo.findAll();
        List<DriverResult> results = new ArrayList<>();

        results.add(new DriverResult(
            user.getUsername(), team.getName(), team.getTeamColor(),
            team.getCarName(), team.effectivePower(), playerScore, lapScores, true));

        Set<Long> usedIds = new HashSet<>();
        usedIds.add(team.getId());
        int aiIdx = 0;
        for (SrcTeam t : allTeams) {
            if (usedIds.contains(t.getId())) continue;
            double aiTire = 100.0, aiOil = 100.0, aiTotal = 0;
            List<Double> aiLaps = new ArrayList<>();
            for (int lap = 0; lap < LAPS; lap++) {
                double s = computeTeamCarScoreWithWear(t, sr, cr, aiTire, aiOil);
                aiLaps.add(s); aiTotal += s;
                aiTire = Math.max(0, aiTire - TIRE_PER_LAP);
                aiOil  = Math.max(0, aiOil  - OIL_PER_LAP);
            }
            results.add(new DriverResult(
                AI_NAMES.get(aiIdx % AI_NAMES.size()), t.getName(), t.getTeamColor(),
                t.getCarName(), t.effectivePower(), aiTotal / LAPS, aiLaps, false));
            usedIds.add(t.getId()); aiIdx++;
            if (results.size() >= TOTAL_DRIVERS) break;
        }
        while (results.size() < TOTAL_DRIVERS) {
            double aiTotal = 0;
            List<Double> aiLaps = new ArrayList<>();
            double aiTire = 100.0, aiOil = 100.0;
            for (int lap = 0; lap < LAPS; lap++) {
                double s = computeGenericScore(sr, cr, aiTire, aiOil);
                aiLaps.add(s); aiTotal += s;
                aiTire = Math.max(0, aiTire - TIRE_PER_LAP);
                aiOil  = Math.max(0, aiOil  - OIL_PER_LAP);
            }
            results.add(new DriverResult("Driver #" + results.size(),
                "Privateer", "#888888", "Privateer Car", 300, aiTotal / LAPS, aiLaps, false));
        }

        results.sort((a, b) -> Double.compare(b.avgScore(), a.avgScore()));
        int playerPos = 1;
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).isPlayer()) { playerPos = i + 1; break; }
        }

        int  pts          = SrcSeason.pointsForPosition(playerPos);
        long raceEarnings = raceEarningsForPosition(playerPos);

        season.setPlayerPoints(season.getPlayerPoints() + pts);
        season.getRacePositions().add(playerPos);
        season.setCurrentRace(season.getCurrentRace() + 1);
        team.setSeasonEarnings(team.getSeasonEarnings() + raceEarnings);
        // Reverse les gains en budget écurie progressivement
        team.setBudget(team.getBudget() + raceEarnings / 2);
        teamRepo.save(team);

        boolean seasonOver = season.getCurrentRace() >= TOTAL_RACES;
        long playerPay = 0;
        if (seasonOver) {
            season.setFinished(true);
            int finalPos = computeFinalPosition(season);
            season.setSeasonPosition(finalPos);
            playerPay = SrcSeason.earningsForPosition(finalPos, team.getSeasonEarnings() / 2);
            season.setPlayerEarnings(playerPay);
            user.setCredits(user.getCredits() + playerPay);
            userRepo.save(user);
            season.setPaid(true);
            team.setSeasonEarnings(0);
            teamRepo.save(team);
        }
        seasonRepo.save(season);

        // Stats voiture finale pour l'écran suivant
        CarStats carStats = new CarStats(
            team.getCarName(), team.effectivePower(), team.getCarWeight() - team.getWeightBonus() * 10,
            team.effectiveGrip(), team.getCarAspiration(), team.getTeamColor(),
            team.getEngineBonus(), team.getGripBonus(), team.getWeightBonus(),
            finalTireWear, finalOilQuality, lapScores
        );

        return ResponseEntity.ok(new SrcRaceResult(
            chosen.name(), chosen.straight(), chosen.corner(),
            results, playerPos, pts,
            raceEarnings, team.getSeasonEarnings(),
            season.getPlayerPoints(), season.getCurrentRace(),
            seasonOver, playerPay, user.getCredits(),
            carStats
        ));
    }

    // ── 6. PAYOUT MANUEL ──────────────────────────────────

    @PostMapping("/season/payout/{userId}")
    public ResponseEntity<?> claimPayout(@PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        seasonRepo.findByUserIdOrderByIdDesc(userId).stream()
            .filter(s -> s.isFinished() && !s.isPaid()).findFirst()
            .ifPresent(s -> {
                user.setCredits(user.getCredits() + s.getPlayerEarnings());
                s.setPaid(true);
                userRepo.save(user); seasonRepo.save(s);
            });
        return ResponseEntity.ok("OK");
    }

    // ══════════════════════════════════════════════════════
    //  SIMULATION ENGINE
    // ══════════════════════════════════════════════════════

    private double computeTeamCarScoreWithWear(SrcTeam team, double sr, double cr,
                                                double tireWear, double oilQuality) {
        double tireMulti = 0.80 + (tireWear / 100.0) * 0.20;
        double oilMulti  = 0.85 + (oilQuality / 100.0) * 0.15;
        double aspBonus  = "TURBO".equalsIgnoreCase(team.getCarAspiration()) ? 1.15 : 1.0;
        int    effectiveWeight = team.getCarWeight() - team.getWeightBonus() * 10;

        double straight = (team.effectivePower() * oilMulti / effectiveWeight) * aspBonus * 100.0;
        double corner   = team.effectiveGrip() * tireMulti * 1.20 * (1000.0 / effectiveWeight) * 100.0;
        double base     = sr * straight + cr * corner;
        double noise    = clamp(1.0 + RNG.nextGaussian() * NOISE, 1.0 - NOISE, 1.0 + NOISE);
        return base * noise;
    }

    private double computeGenericScore(double sr, double cr, double tireWear, double oilQuality) {
        double tireMulti = 0.80 + (tireWear / 100.0) * 0.20;
        double oilMulti  = 0.85 + (oilQuality / 100.0) * 0.15;
        double straight  = (280.0 * oilMulti / 1400.0) * 1.15 * 100.0;
        double corner    = 1.35 * tireMulti * 1.20 * (1000.0 / 1400.0) * 100.0;
        double noise     = clamp(1.0 + RNG.nextGaussian() * NOISE, 0.88, 1.12);
        return (sr * straight + cr * corner) * noise;
    }

    private double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }

    private long raceEarningsForPosition(int pos) {
        return switch (pos) {
            case 1  -> 80000; case 2  -> 55000; case 3  -> 40000;
            case 4  -> 30000; case 5  -> 22000; case 6  -> 16000;
            case 7  -> 12000; case 8  -> 8000;  case 9  -> 5000;
            case 10 -> 3000;  default -> 1000;
        };
    }

    private int computeFinalPosition(SrcSeason season) {
        int pts = season.getPlayerPoints();
        if (pts >= 200) return 1; if (pts >= 160) return 2; if (pts >= 130) return 3;
        if (pts >= 100) return 4; if (pts >= 75)  return 5; if (pts >= 55)  return 6;
        if (pts >= 40)  return 7; if (pts >= 28)  return 8; if (pts >= 18)  return 9;
        if (pts >= 10)  return 10; return 11 + RNG.nextInt(9);
    }

    private long estimatedSeasonEarnings(SrcTeam t) {
        return Math.max(500000L - (t.getRank() - 1) * 40000L, 80000L);
    }

    private TeamInvitation toInvitation(SrcTeam t) {
        return new TeamInvitation(t.getId(), t.getName(), t.getRank(),
            t.getCarName(), t.effectivePower(), t.getCarWeight() - t.getWeightBonus() * 10,
            t.effectiveGrip(), t.getCarAspiration(), t.getTeamColor(),
            t.getBudget(), t.getDescription(), t.getReputationRequired(),
            estimatedSeasonEarnings(t));
    }

    private SeasonState toSeasonState(SrcSeason s) {
        SrcTeam t = s.getTeam();
        return new SeasonState(s.getId(), t.getName(), t.getTeamColor(),
            t.getCarName(), t.effectivePower(), t.getCarWeight() - t.getWeightBonus() * 10,
            t.effectiveGrip(), t.getEngineBonus(), t.getGripBonus(), t.getWeightBonus(),
            s.getCurrentRace(), TOTAL_RACES, s.getPlayerPoints(),
            s.isFinished(), s.getPlayerEarnings(),
            s.getRacePositions(), t.getBudget(), t.getSeasonEarnings());
    }

    private static final List<String> AI_NAMES = List.of(
        "K. Tanaka","H. Suzuki","R. Yamamoto","T. Nakamura","Y. Kobayashi",
        "S. Ito","M. Watanabe","A. Sato","D. Kimura","J. Matsumoto",
        "K. Inoue","T. Hayashi","R. Shimizu","S. Yamaguchi","H. Ogawa",
        "A. Fujita","M. Goto","K. Hasegawa","T. Mori","S. Okamoto"
    );

    // ══════════════════════════════════════════════════════
    //  DTOs & Records
    // ══════════════════════════════════════════════════════

    record SrcCircuit(String name, int straight, int corner) {}

    public record JoinRequest(long userId, long teamId) {}
    public record SrcRaceRequest(long userId) {}
    public record UpgradeRequest(long userId, String type) {}

    public record TeamInvitation(
        long id, String name, int rank,
        String carName, int carPower, int carWeight,
        double carGrip, String carAspiration, String teamColor,
        long budget, String description, int reputationRequired,
        long estimatedSeasonEarnings
    ) {}

    public record DriverResult(
        String driverName, String teamName, String teamColor,
        String carName, int carPower,
        double avgScore, List<Double> lapScores, boolean isPlayer
    ) {}

    public record CarStats(
        String carName, int power, int weight, double grip,
        String aspiration, String teamColor,
        int engineBonus, int gripBonus, int weightBonus,
        double finalTireWear, double finalOilQuality,
        List<Double> lapScores
    ) {}

    public record UpgradeOption(String type, String name, String description, long cost, boolean canAfford) {}
    public record UpgradeMenu(String teamName, String teamColor, long budget,
                               int power, double grip, int weight,
                               List<UpgradeOption> options) {}

    public record SrcRaceResult(
        String raceName, int straightLine, int corner,
        List<DriverResult> results, int playerPosition, int pointsEarned,
        long raceTeamEarnings, long totalTeamEarnings,
        int totalPoints, int raceNumber,
        boolean seasonOver, long playerPayout, long newBalance,
        CarStats carStats
    ) {}

    public record StandingEntry(String driver, String team, int points, boolean isPlayer) {}

    public record SeasonState(
        long seasonId, String teamName, String teamColor,
        String carName, int carPower, int carWeight, double carGrip,
        int engineBonus, int gripBonus, int weightBonus,
        int currentRace, int totalRaces, int playerPoints,
        boolean finished, long playerEarnings,
        List<Integer> racePositions,
        long teamBudget, long teamSeasonEarnings
    ) {}
}