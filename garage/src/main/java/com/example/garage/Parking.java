package com.example.garage;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "parkings") // "user" est souvent un mot réservé en SQL, on utilise "users"
@Data
public class Parking {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String name;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Racers> racers = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Race> races = new ArrayList<>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Racers> getRacers() {
		return racers;
	}

	public void setRacers(ArrayList<Racers> racers) {
		this.racers = racers;
	}

	public List<Race> getRace() {
		return races;
	}

	public void setRace(ArrayList<Race> race) {
		this.races = race;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void add(Race race) {
		races.add(race);
	}
	
	
}
