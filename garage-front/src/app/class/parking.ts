import { Racers } from "./racers";
import { Races } from "./races";

export class Parking {

    id!: number;
    name!: string;
    races: Races[] = [];
    racers: Racers[] = [];
}
