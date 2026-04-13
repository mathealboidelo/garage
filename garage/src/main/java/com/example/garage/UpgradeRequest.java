package com.example.garage;

/**
 * Payload pour acheter un upgrade.
 *  userId      – joueur qui paie
 *  carId       – voiture à upgrader
 *  upgradeType – catégorie (ENGINE, TRANSMISSION, etc.)
 */
public record UpgradeRequest(long userId, long carId, String upgradeType) {}
