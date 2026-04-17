package com.example.garage;

import jakarta.persistence.*;

/**
 * Circuit de course.
 *
 * Représenté par une liste de segments alternés :
 *   straight(m) → corner(angle°) → straight(m) → corner(angle°) → ...
 *
 * Les segments sont encodés dans la colonne `segments` sous forme de chaîne :
 *   "S:300,C:90,S:500,C:45,S:200,C:120,S:400,C:60"
 *   S = straight (longueur en mètres fictifs)
 *   C = corner   (angle en degrés : 30=rapide, 90=moyen, 150=lent)
 *
 * Les anciennes colonnes straigthLine et corner sont conservées pour
 * la rétrocompatibilité avec le frontend (affichage pourcentages).
 */
@Entity
@Table(name = "races")
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    // Anciens champs conservés pour l'affichage front (% droite / % virage)
    private int straigthLine;
    private int corner;

    // Nouveaux champs : segments détaillés
    @Column(length = 512)
    private String segments; // ex: "S:400,C:90,S:300,C:60,S:500,C:120"

    public long   getId()                   { return id; }
    public void   setId(long id)            { this.id = id; }
    public String getName()                 { return name; }
    public void   setName(String n)         { this.name = n; }
    public int    getStraigthLine()         { return straigthLine; }
    public void   setStraigthLine(int v)    { this.straigthLine = v; }
    public int    getCorner()               { return corner; }
    public void   setCorner(int v)          { this.corner = v; }
    public String getSegments()             { return segments; }
    public void   setSegments(String v)     { this.segments = v; }
}
