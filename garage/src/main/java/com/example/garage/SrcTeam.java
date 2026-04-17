package com.example.garage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Écurie du Street Racing Championship.
 * Chaque écurie possède une voiture JGTC avec des stats propres.
 * Le rang (1=meilleur, 10=moins bon) détermine les performances.
 */
@Entity
@Table(name = "src_teams")
public class SrcTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int    rank;               // 1 = top team, 10 = backmarker
    private int    reputationRequired; // rep minimale pour recevoir une invitation

    // Voiture JGTC de l'écurie
    private String carName;
    private int    carPower;
    private int    carWeight;
    private double carGrip;
    private String carAspiration;      // TURBO / NATURAL
    private String teamColor;          // couleur hex pour l'UI

    // Finances de l'écurie
    private long   budget;             // budget disponible pour améliorer la voiture
    private long   seasonEarnings;     // gains de la saison en cours

    // Bonus de performance achetés avec le budget
    private int    engineBonus;        // +CH achetés (max 100)
    private int    gripBonus;          // +grip acheté (max 0.20 = gripBonus 20)
    private int    weightBonus     = 0;  // -10kg par palier (max 8 = -80kg)

    // Description / lore
    private String description;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SrcSeason> seasons = new ArrayList<>();

    // Getters / Setters
    public Long   getId()                    { return id; }
    public void   setId(Long id)             { this.id = id; }
    public String getName()                  { return name; }
    public void   setName(String v)          { this.name = v; }
    public int    getRank()                  { return rank; }
    public void   setRank(int v)             { this.rank = v; }
    public int    getReputationRequired()    { return reputationRequired; }
    public void   setReputationRequired(int v){ this.reputationRequired = v; }
    public String getCarName()               { return carName; }
    public void   setCarName(String v)       { this.carName = v; }
    public int    getCarPower()              { return carPower; }
    public void   setCarPower(int v)         { this.carPower = v; }
    public int    getCarWeight()             { return carWeight; }
    public void   setCarWeight(int v)        { this.carWeight = v; }
    public double getCarGrip()               { return carGrip; }
    public void   setCarGrip(double v)       { this.carGrip = v; }
    public String getCarAspiration()         { return carAspiration; }
    public void   setCarAspiration(String v) { this.carAspiration = v; }
    public String getTeamColor()             { return teamColor; }
    public void   setTeamColor(String v)     { this.teamColor = v; }
    public long   getBudget()                { return budget; }
    public void   setBudget(long v)          { this.budget = v; }
    public long   getSeasonEarnings()        { return seasonEarnings; }
    public void   setSeasonEarnings(long v)  { this.seasonEarnings = v; }
    public int    getEngineBonus()           { return engineBonus; }
    public void   setEngineBonus(int v)      { this.engineBonus = v; }
    public int    getGripBonus()             { return gripBonus; }
    public void   setGripBonus(int v)        { this.gripBonus = v; }
    public String getDescription()           { return description; }
    public void   setDescription(String v)   { this.description = v; }
    public List<SrcSeason> getSeasons()      { return seasons; }

    /** Puissance effective de la voiture (base + bonus achetés) */
    public int effectivePower() { return carPower + engineBonus; }
    /** Grip effectif */
    public double effectiveGrip() { return carGrip + gripBonus * 0.01; }
	public int getWeightBonus() {
		return weightBonus;
	}
	public void setWeightBonus(int weightBonus) {
		this.weightBonus = weightBonus;
	}
}