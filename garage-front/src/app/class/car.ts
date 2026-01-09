export class Car {
    id!: number;
    name!: string;
    power!: number;           // Puissance en CV
    gripModifier!: number;    // Multiplicateur d'adhérence (ex: 1.1)
    weight!: number;          // Poids en kg
    aspiration!: string;      // "NATURAL" ou "TURBO"
    tireType!: string;        // "Slick", "Sport", etc.
    price!: number;
}
