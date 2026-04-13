import { Races } from "./races";

export class Parking {
  id!: number;
  name!: string;
  races: Races[] = [];
  racers: any[] = [];   // RacerView[] quand chargé via getParkingForUser
}
