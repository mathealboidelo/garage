package com.example.garage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private CarRepository carRepository;
	
	@GetMapping("/api/player")
	public User getPlayer() {
        // Attention: crash si la base est vide, il faudra gérer ça plus tard
		return userRepository.findAll().get(0); 
	}
	
    // 2. Pour la Liste des utilisateurs (Renvoie TOUT le monde)
    // J'ai changé l'URL en "/api/users" (pluriel) pour différencier
	@GetMapping("/api/users")
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	
	
    // 3. Pour ajouter un utilisateur
	@PostMapping("/api/users")
	public User addUser (@RequestBody User user) {
		user.setLevel(1);
		user.setCredits(10000);
		return userRepository.save(user);
	}
	
	@GetMapping("/api/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
	    return userRepository.findById(id)
	            .map(user -> ResponseEntity.ok().body(user))
	            .orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/api/deleteuser/{id}")
	public void deleteUser(@PathVariable Long id) {
		userRepository.deleteById(id);
	}
	
	@PostMapping("/api/buy")
	public void addCarToGarage(@RequestBody BuyRequest request) {
		User user = userRepository.findById(request.userId()).orElseThrow();
	    Car car = carRepository.findById(request.carId()).orElseThrow();
	    
	    user.getGarage().add(car);
	}
	

}
