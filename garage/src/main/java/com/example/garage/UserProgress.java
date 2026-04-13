package com.example.garage;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Progression du joueur : quels membres de gang ont été battus.
 * Une ligne par joueur. Les IDs des Racers battus sont stockés
 * dans une collection séparée (table user_defeated_racers).
 */
@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    /** IDs des Racers (membres de gang + boss) déjà battus par ce joueur */
    @ElementCollection
    @CollectionTable(name = "user_defeated_racers",
                     joinColumns = @JoinColumn(name = "progress_id"))
    @Column(name = "racer_id")
    private Set<Long> defeatedRacerIds = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Set<Long> getDefeatedRacerIds() { return defeatedRacerIds; }
    public void setDefeatedRacerIds(Set<Long> ids) { this.defeatedRacerIds = ids; }

    public void addDefeated(long racerId) { this.defeatedRacerIds.add(racerId); }
    public boolean hasDefeated(long racerId) { return this.defeatedRacerIds.contains(racerId); }
}