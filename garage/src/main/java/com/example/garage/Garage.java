package com.example.garage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Garage") // "user" est souvent un mot réservé en SQL, on utilise "users"
@Data // Génère les getters/setters automatiquement avec Lombok
public class Garage {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	// Un garage contient plusieurs voitures
    // mappedBy dit que c'est le champ "garage" dans la classe Car qui gère la relation
    // cascade = ALL permet de supprimer les voitures si le garage est supprimé
    @OneToMany(mappedBy = "garage", cascade = CascadeType.ALL)
    private List<Car> cars = new ArrayList<>();

    @OneToOne(mappedBy = "garage")
    @JsonIgnore
    private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Car> getCars() {
		return cars;
	}

	public void setCars(List<Car> cars) {
		this.cars = cars;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void add(Car car) {
		cars.add(car);
	}
    
    
}
