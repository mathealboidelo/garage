package com.example.garage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, DealershipRepository dealershipRepository, CarRepository carRepository) {
        return args -> {
            
            // --- PARTIE 1 : INITIALISATION JOUEUR ---
            if (userRepository.count() == 0) {
                System.out.println("--- Initialisation du Joueur ---");
                User player = new User();
                player.setUsername("RacingLegend_01");
                player.setCredits(10000);
                player.setLevel(1);
                
                // On crée aussi un Garage vide pour le joueur (évite les NullPointer plus tard)
                Garage playerGarage = new Garage();
                player.setGarage(playerGarage); // Liaison User -> Garage
                // playerGarage.setUser(player); // Liaison Garage -> User (si tu as mis mappedBy correctement)

                userRepository.save(player); // Le cascade va sauver le garage aussi
                System.out.println("Joueur créé avec succès.");
            }

            // --- PARTIE 2 : INITIALISATION DEALERSHIP ---
            if (dealershipRepository.count() == 0) {
                System.out.println("--- Initialisation du Concessionnaire ---");

                Dealership dealership = new Dealership();
                dealership.setName("Super Cars Shop");
                
                // IMPORTANT : On initialise la liste pour éviter le NullPointerException
                if (dealership.getCars() == null) {
                    dealership.setCars(new ArrayList<>());
                }

                // On prépare les voitures SANS les sauvegarder individuellement (repo.save)
                // On laisse le Dealership s'en occuper via le CascadeType.ALL
                createAndLinkCar(dealership, "Sport GT 2000", 150, 1200, 9000, AspirationType.TURBO);
                createAndLinkCar(dealership, "Compacte Rallye", 110, 1050, 14000, AspirationType.NATURAL);
                createAndLinkCar(dealership, "Muscle Car Classic", 300, 1600, 25000, AspirationType.NATURAL);

                // On sauvegarde UNIQUEMENT le dealership. 
                // Grâce au CascadeType.ALL, cela va créer le dealership ET les 3 voitures d'un coup.
                dealershipRepository.save(dealership);
                
                System.out.println("Concessionnaire et voitures créés.");
            } else {
                System.out.println("Le concessionnaire existe déjà.");
            }
        };
    }
    
    // Méthode utilitaire modifiée : elle ne fait plus de repo.save()
    // Elle relie juste les objets Java entre eux
    private void createAndLinkCar(Dealership dealer, String name, int hp, int weight, long price, AspirationType asp) {
        Car c = new Car();
        c.setName(name);
        c.setPower(hp);
        c.setWeight(weight);
        c.setPrice(price);
        c.setAspiration(asp);
        c.setTireType("Sport");
        c.setGripModifier(1.1);
        
        // --- LA CLÉ DU SUCCÈS EST ICI ---
        c.setGarage(null); // Pas dans un garage joueur
        
        // 1. On dit à la voiture : "Tu appartiens à ce dealership"
        c.setDealership(dealer); 
        
        // 2. On dit au dealership : "Ajoute cette voiture à ta liste"
        dealer.getCars().add(c);
    }
}