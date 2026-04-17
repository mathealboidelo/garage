package com.example.garage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "car_upgrades")
public class CarUpgrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "car_id", unique = true)
    @JsonIgnore  // évite la boucle CarUpgrade → Car → Garage → Cars → CarUpgrade
    private Car car;

    private int engineLevel       = 0;
    private int transmissionLevel = 0;
    private int suspensionLevel   = 0;
    private int brakesLevel       = 0;
    private int weightLevel       = 0;
    private int tiresLevel        = 0;

    public Long getId()             { return id; }
    public void setId(Long id)      { this.id = id; }
    public Car  getCar()            { return car; }
    public void setCar(Car car)     { this.car = car; }

    public int  getEngineLevel()           { return engineLevel; }
    public void setEngineLevel(int v)      { this.engineLevel = v; }
    public int  getTransmissionLevel()     { return transmissionLevel; }
    public void setTransmissionLevel(int v){ this.transmissionLevel = v; }
    public int  getSuspensionLevel()       { return suspensionLevel; }
    public void setSuspensionLevel(int v)  { this.suspensionLevel = v; }
    public int  getBrakesLevel()           { return brakesLevel; }
    public void setBrakesLevel(int v)      { this.brakesLevel = v; }
    public int  getWeightLevel()           { return weightLevel; }
    public void setWeightLevel(int v)      { this.weightLevel = v; }
    public int  getTiresLevel()            { return tiresLevel; }
    public void setTiresLevel(int v)       { this.tiresLevel = v; }
}
