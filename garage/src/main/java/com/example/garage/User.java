package com.example.garage;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
	
@Entity
@Table(name = "users") // "user" est souvent un mot réservé en SQL, on utilise "users"
@Data // Génère les getters/setters automatiquement avec Lombok
public class User {

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getCredits() {
		return credits;
	}

	public void setCredits(long credits) {
		this.credits = credits;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	private long credits;

    private int level;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "garage_id", referencedColumnName = "id")
    private Garage garage;

	public Garage getGarage() {
		return garage;
	}

	public void setGarage(Garage garage) {
		this.garage = garage;
	}
    
    

    // Pour l'instant, on laisse la liste de voitures de côté 
    // jusqu'à ce qu'on crée la classe Car, mais garde l'idée en tête.
}
