package com.example.garage;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private long  credits;
    private int   level;
    private int   reputation = 0;   // Réputation gagnée en course
    private int   wins       = 0;   // Nombre de victoires totales

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "garage_id", referencedColumnName = "id")
    private Garage garage;

    // Getters / Setters
    public Long   getId()           { return id; }
    public void   setId(Long id)    { this.id = id; }

    public String getUsername()              { return username; }
    public void   setUsername(String u)      { this.username = u; }

    public long   getCredits()               { return credits; }
    public void   setCredits(long c)         { this.credits = c; }

    public int    getLevel()                 { return level; }
    public void   setLevel(int l)            { this.level = l; }

    public int    getReputation()            { return reputation; }
    public void   setReputation(int r)       { this.reputation = r; }

    public int    getWins()                  { return wins; }
    public void   setWins(int w)             { this.wins = w; }

    public Garage getGarage()                { return garage; }
    public void   setGarage(Garage g)        { this.garage = g; }
}