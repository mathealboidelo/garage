package com.example.garage;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Car") // "user" est souvent un mot réservé en SQL, on utilise "users"
@Data // Génère les getters/setters automatiquement avec Lombok
public class Car {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int power;           // Puissance en CV
    private double gripModifier; // Ex: 1.2 pour +20% d'adhérence
    private int weight;          // Poids en kg
    private long price;
    
    @Enumerated(EnumType.STRING)
    private AspirationType aspiration;
    
    private String tireType;     // Ex: "Slick", "Sport", "Rain"

    @ManyToOne
    @JoinColumn(name = "garage_id")
    @JsonIgnore
    private Garage garage;
    
    @ManyToOne
    @JoinColumn(name = "dealership_id")
    @JsonIgnore
    private Dealership dealership;

	public Dealership getDealership() {
		return dealership;
	}

	public void setDealership(Dealership dealership) {
		this.dealership = dealership;
	}

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

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public double getGripModifier() {
		return gripModifier;
	}

	public void setGripModifier(double gripModifier) {
		this.gripModifier = gripModifier;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public AspirationType getAspiration() {
		return aspiration;
	}

	public void setAspiration(AspirationType aspiration) {
		this.aspiration = aspiration;
	}

	public String getTireType() {
		return tireType;
	}

	public void setTireType(String tireType) {
		this.tireType = tireType;
	}

	public Garage getGarage() {
		return garage;
	}

	public void setGarage(Garage garage) {
		this.garage = garage;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}
	
	
    
    
}
