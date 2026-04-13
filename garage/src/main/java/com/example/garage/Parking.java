package com.example.garage;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parkings")
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "parkings_racers",
        joinColumns = @JoinColumn(name = "parking_id"),
        inverseJoinColumns = @JoinColumn(name = "racer_id"))
    private List<Racers> racers = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "parkings_races",
        joinColumns = @JoinColumn(name = "parking_id"),
        inverseJoinColumns = @JoinColumn(name = "race_id"))
    private List<Race> races = new ArrayList<>();

    public long   getId()               { return id; }
    public void   setId(long id)        { this.id = id; }
    public String getName()             { return name; }
    public void   setName(String n)     { this.name = n; }
    public List<Racers> getRacers()     { return racers; }
    public void   setRacers(List<Racers> r) { this.racers = r; }
    public List<Race>   getRace()       { return races; }
    public void   setRace(List<Race> r) { this.races = r; }
    public void   add(Race race)        { races.add(race); }
}