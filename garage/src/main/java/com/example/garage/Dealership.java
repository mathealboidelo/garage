package com.example.garage;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dealership")
public class Dealership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "dealership", cascade = CascadeType.ALL)
    private List<Car> cars = new ArrayList<>();

    public Long        getId()              { return id; }
    public void        setId(Long id)       { this.id = id; }
    public String      getName()            { return name; }
    public void        setName(String n)    { this.name = n; }
    public List<Car>   getCars()            { return cars; }
    public void        setCars(List<Car> c) { this.cars = c; }
}