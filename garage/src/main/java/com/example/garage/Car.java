package com.example.garage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "Car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int    power;           // CH
    private double gripModifier;
    private int    weight;          // kg
    private long   price;

    @Enumerated(EnumType.STRING)
    private AspirationType aspiration;

    private String tireType;

    // ── Wear & Maintenance ────────────────────────────────
    /** Usure des pneus : 100 = neuf, 0 = complètement usé */
    private double tireWear    = 100.0;

    /** Qualité de l'huile : 100 = neuf, 0 = à vider de toute urgence */
    private double oilQuality  = 100.0;

    /** Nombre de courses effectuées avec cette voiture */
    private int racesCount     = 0;

    @ManyToOne
    @JoinColumn(name = "garage_id")
    @JsonIgnore
    private Garage garage;

    @ManyToOne
    @JoinColumn(name = "dealership_id")
    @JsonIgnore
    private Dealership dealership;

    // ── Getters / Setters ──────────────────────────────────
    public Long   getId()                    { return id; }
    public void   setId(Long id)             { this.id = id; }
    public String getName()                  { return name; }
    public void   setName(String n)          { this.name = n; }
    public int    getPower()                 { return power; }
    public void   setPower(int p)            { this.power = p; }
    public double getGripModifier()          { return gripModifier; }
    public void   setGripModifier(double g)  { this.gripModifier = g; }
    public int    getWeight()                { return weight; }
    public void   setWeight(int w)           { this.weight = w; }
    public long   getPrice()                 { return price; }
    public void   setPrice(long p)           { this.price = p; }
    public AspirationType getAspiration()    { return aspiration; }
    public void   setAspiration(AspirationType a) { this.aspiration = a; }
    public String getTireType()              { return tireType; }
    public void   setTireType(String t)      { this.tireType = t; }
    public double getTireWear()              { return tireWear; }
    public void   setTireWear(double v)      { this.tireWear = v; }
    public double getOilQuality()            { return oilQuality; }
    public void   setOilQuality(double v)    { this.oilQuality = v; }
    public int    getRacesCount()            { return racesCount; }
    public void   setRacesCount(int v)       { this.racesCount = v; }
    public Garage getDealership_garage()     { return garage; }
    public Garage getGarage()               { return garage; }
    public void   setGarage(Garage g)        { this.garage = g; }
    public Dealership getDealership()        { return dealership; }
    public void   setDealership(Dealership d){ this.dealership = d; }
}