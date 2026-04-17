package com.example.garage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Saison SRC d'un joueur.
 * Une saison = 10 courses, une écurie, un classement.
 */
@Entity
@Table(name = "src_seasons")
public class SrcSeason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private SrcTeam team;

    private int    currentRace     = 0;   // 0-9 (10 courses par saison)
    private int    playerPoints    = 0;   // points accumulés
    private long   playerEarnings  = 0;   // gains du joueur cette saison
    private boolean finished       = false;
    private boolean paid           = false; // gains versés au joueur

    // Classement saison (points cumulés)
    private int    seasonPosition  = 0;   // position finale

    @ElementCollection
    @CollectionTable(name = "src_race_positions",
                     joinColumns = @JoinColumn(name = "season_id"))
    @Column(name = "position")
    private List<Integer> racePositions = new ArrayList<>(); // position à chaque course

    // Getters / Setters
    public Long    getId()                     { return id; }
    public void    setId(Long v)               { this.id = v; }
    public User    getUser()                   { return user; }
    public void    setUser(User v)             { this.user = v; }
    public SrcTeam getTeam()                   { return team; }
    public void    setTeam(SrcTeam v)          { this.team = v; }
    public int     getCurrentRace()            { return currentRace; }
    public void    setCurrentRace(int v)       { this.currentRace = v; }
    public int     getPlayerPoints()           { return playerPoints; }
    public void    setPlayerPoints(int v)      { this.playerPoints = v; }
    public long    getPlayerEarnings()         { return playerEarnings; }
    public void    setPlayerEarnings(long v)   { this.playerEarnings = v; }
    public boolean isFinished()                { return finished; }
    public void    setFinished(boolean v)      { this.finished = v; }
    public boolean isPaid()                    { return paid; }
    public void    setPaid(boolean v)          { this.paid = v; }
    public int     getSeasonPosition()         { return seasonPosition; }
    public void    setSeasonPosition(int v)    { this.seasonPosition = v; }
    public List<Integer> getRacePositions()    { return racePositions; }
    public void    setRacePositions(List<Integer> v) { this.racePositions = v; }

    /** Points F1-style selon position (1er=25, 2e=18, ... 10e=1, au-delà=0) */
    public static int pointsForPosition(int pos) {
        return switch (pos) {
            case 1  -> 25; case 2  -> 18; case 3  -> 15;
            case 4  -> 12; case 5  -> 10; case 6  -> 8;
            case 7  -> 6;  case 8  -> 4;  case 9  -> 2;
            case 10 -> 1;  default -> 0;
        };
    }

    /** Gain joueur selon position (versé en fin de saison) */
    public static long earningsForPosition(int pos, long teamBudgetShare) {
        double ratio = switch (pos) {
            case 1  -> 0.40; case 2  -> 0.30; case 3  -> 0.22;
            case 4  -> 0.16; case 5  -> 0.12; case 6  -> 0.09;
            case 7  -> 0.07; case 8  -> 0.05; case 9  -> 0.03;
            case 10 -> 0.02; default -> 0.01;
        };
        return (long)(teamBudgetShare * ratio);
    }
}