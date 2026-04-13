package com.example.garage;

import jakarta.persistence.*;

@Entity
@Table(name = "races")
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private int    straigthLine;
    private int    corner;

    public long   getId()                   { return id; }
    public void   setId(long id)            { this.id = id; }
    public String getName()                 { return name; }
    public void   setName(String n)         { this.name = n; }
    public int    getStraigthLine()         { return straigthLine; }
    public void   setStraigthLine(int v)    { this.straigthLine = v; }
    public int    getCorner()               { return corner; }
    public void   setCorner(int v)          { this.corner = v; }
}