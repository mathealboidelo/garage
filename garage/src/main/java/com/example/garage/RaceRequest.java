package com.example.garage;

/**
 * Payload pour lancer une course.
 *  raceId   : ID de la course générée aléatoirement (via /api/race/generate)
 *  betCar   : true = on mise sa voiture
 *  wagerCarId : id de la voiture mise en jeu (0 si pas de pari voiture)
 */
public record RaceRequest(
    long    userId,
    long    carId,
    long    opponentId,
    long    raceId,
    long    bet,
    boolean betCar,
    long    wagerCarId
) {}
