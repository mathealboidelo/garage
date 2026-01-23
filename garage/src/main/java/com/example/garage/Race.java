package com.example.garage;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "races") // "user" est souvent un mot réservé en SQL, on utilise "users"
@Data // Génère les getters/setters automatiquement avec Lombok
public class Race{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String name;
	
	private int straigthLine;
	
	private int corner;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStraigthLine() {
		return straigthLine;
	}

	public void setStraigthLine(int straigthLine) {
		this.straigthLine = straigthLine;
	}

	public int getCorner() {
		return corner;
	}

	public void setCorner(int corner) {
		this.corner = corner;
	}
	
	
}
