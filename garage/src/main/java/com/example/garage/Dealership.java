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
@Table(name = "Dealership")
@Data
public class Dealership {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String name;
	
	// Un garage contient plusieurs voitures
    // mappedBy dit que c'est le champ "garage" dans la classe Car qui gère la relation
    // cascade = ALL permet de supprimer les voitures si le garage est supprimé
    @OneToMany(mappedBy = "dealership", cascade = CascadeType.ALL)
    private List<Car> cars = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Car> getCars() {
		return cars;
	}

	public void setCars(List<Car> cars) {
		this.cars = cars;
	}
    
    
}
