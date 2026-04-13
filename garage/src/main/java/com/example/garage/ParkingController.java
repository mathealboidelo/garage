package com.example.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Retourne les parkings avec la logique de visibilité :
 *  - Membres de gang : toujours visibles
 *  - Boss : visible seulement si tous les membres du gang du parking sont battus
 *  - Adversaires spéciaux : visibles si reputation >= reputationRequired
 *  - Adversaires random : toujours visibles
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ParkingController {

    @Autowired private ParkingRepository      parkingRepo;
    @Autowired private UserProgressRepository progressRepo;
    @Autowired private UserRepository         userRepo;

    @GetMapping("/api/parkings")
    public List<Parking> getAllParkings() {
        return parkingRepo.findAll();
    }

    /**
     * Retourne le parking enrichi avec la visibilité des racers
     * selon la progression du joueur.
     */
    @GetMapping("/api/parkings/{id}/user/{userId}")
    public ResponseEntity<ParkingView> getParkingForUser(
            @PathVariable Long id, @PathVariable Long userId) {

        Parking parking = parkingRepo.findById(id).orElseThrow();
        User    user    = userRepo.findById(userId).orElseThrow();
        UserProgress progress = progressRepo.findByUserId(userId).orElseGet(() -> {
            UserProgress p = new UserProgress(); p.setUser(user);
            return progressRepo.save(p);
        });

        List<RacerView> visible = new ArrayList<>();
        Set<Long> defeated = progress.getDefeatedRacerIds();

        // Trie : membres d'abord, boss ensuite, random/spéciaux à la fin
        List<Racers> gangMembers = parking.getRacers().stream()
                .filter(r -> r.isGangMember() && !r.isBoss()).toList();
        List<Racers> bosses      = parking.getRacers().stream()
                .filter(Racers::isBoss).toList();
        List<Racers> others      = parking.getRacers().stream()
                .filter(r -> !r.isGangMember() && !r.isBoss()).toList();

        // Membres de gang — toujours visibles, marqués comme battus si applicable
        for (Racers r : gangMembers) {
            visible.add(new RacerView(r, defeated.contains(r.getId()), true, false));
        }

        // Boss — visible seulement si tous les membres du même gang sont battus
        for (Racers boss : bosses) {
            boolean allMembersDefeated = gangMembers.stream()
                    .filter(m -> m.getGangName().equals(boss.getGangName()))
                    .allMatch(m -> defeated.contains(m.getId()));
            if (allMembersDefeated) {
                visible.add(new RacerView(boss, defeated.contains(boss.getId()), true, true));
            }
        }

        // Adversaires random / spéciaux
        for (Racers r : others) {
            if (r.isSpecial() && user.getReputation() < r.getReputationRequired()) continue;
            visible.add(new RacerView(r, defeated.contains(r.getId()), false, false));
        }

        return ResponseEntity.ok(new ParkingView(
            parking.getId(), parking.getName(),
            parking.getRace(), visible,
            user.getReputation()
        ));
    }

    // ── View DTOs ──────────────────────────────────────────
    public record RacerView(
        long   id, String displayName, String carName, int carPower,
        String carAspiration, String carTireType, double carGripModifier,
        boolean defeated, boolean isGang, boolean isBoss, boolean isSpecial,
        int reputationRequired, String gangName, String prefix,
        Long specialCarId
    ) {
        public RacerView(Racers r, boolean defeated, boolean isGang, boolean isBoss) {
            this(
                r.getId(), r.getDisplayName(),
                r.getCar().getName(), r.getCar().getPower(),
                r.getCar().getAspiration().name(), r.getCar().getTireType(),
                r.getCar().getGripModifier(),
                defeated, isGang, isBoss, r.isSpecial(),
                r.getReputationRequired(), r.getGangName(), r.getPrefix(),
                r.getSpecialCarForSale() != null ? r.getSpecialCarForSale().getId() : null
            );
        }
    }

    public record ParkingView(
        long id, String name,
        List<Race> races,
        List<RacerView> racers,
        int userReputation
    ) {}
}
