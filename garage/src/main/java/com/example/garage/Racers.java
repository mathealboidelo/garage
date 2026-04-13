package com.example.garage;

import jakarta.persistence.*;

@Entity
@Table(name = "racers")
public class Racers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    // ManyToOne : plusieurs adversaires peuvent partager le même modèle de voiture
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    private String  prefix              = "";
    private boolean gangMember          = false;
    private boolean boss                = false;
    private String  gangName            = "";
    private boolean special             = false;
    private int     reputationRequired  = 0;

    @ManyToOne
    @JoinColumn(name = "special_car_id")
    private Car specialCarForSale = null;

    public long   getId()                        { return id; }
    public void   setId(long id)                 { this.id = id; }
    public String getName()                      { return name; }
    public void   setName(String n)              { this.name = n; }
    public Car    getCar()                        { return car; }
    public void   setCar(Car c)                  { this.car = c; }
    public String getPrefix()                    { return prefix; }
    public void   setPrefix(String p)            { this.prefix = p; }
    public boolean isGangMember()                { return gangMember; }
    public void   setGangMember(boolean b)       { this.gangMember = b; }
    public boolean isBoss()                      { return boss; }
    public void   setBoss(boolean b)             { this.boss = b; }
    public String getGangName()                  { return gangName; }
    public void   setGangName(String g)          { this.gangName = g; }
    public boolean isSpecial()                   { return special; }
    public void   setSpecial(boolean b)          { this.special = b; }
    public int    getReputationRequired()        { return reputationRequired; }
    public void   setReputationRequired(int r)   { this.reputationRequired = r; }
    public Car    getSpecialCarForSale()         { return specialCarForSale; }
    public void   setSpecialCarForSale(Car c)    { this.specialCarForSale = c; }

    public String getDisplayName() {
        return (prefix != null && !prefix.isEmpty()) ? prefix + " " + name : name;
    }
}