package com.example.garage;

/**
 * Catégories d'upgrades disponibles pour une voiture.
 * Chaque catégorie a 3 niveaux : STOCK (0), SPORT (1), RACING (2).
 */
public enum UpgradeType {
    ENGINE,       // Moteur       → +power
    TRANSMISSION, // Transmission → +power (courbes), léger -weight
    SUSPENSION,   // Suspension   → +gripModifier
    BRAKES,       // Freins       → +gripModifier (freinage)
    WEIGHT,       // Allègement   → -weight
    TIRES         // Pneus        → tireType upgrade + gripModifier
}
