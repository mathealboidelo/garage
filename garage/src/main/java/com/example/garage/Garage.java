package com.example.garage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Garage")
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "garage", cascade = CascadeType.ALL)
    private List<Car> cars = new ArrayList<>();

    @OneToOne(mappedBy = "garage")
    @JsonIgnore
    private User user;

    public Long        getId()            { return id; }
    public void        setId(Long id)     { this.id = id; }
    public List<Car>   getCars()          { return cars; }
    public void        setCars(List<Car> c){ this.cars = c; }
    public User        getUser()          { return user; }
    public void        setUser(User u)    { this.user = u; }
    public void        add(Car car)       { cars.add(car); }
}