import { Car } from "./car";

export class Dealership {
    id!: number;
    name!: string;
    cars: Car[] = [];
}
