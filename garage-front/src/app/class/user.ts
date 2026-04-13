import { Garage } from "./garage";

export class User {
  id!: number;
  username!: string;
  credits!: number;
  level!: number;
  reputation!: number;
  wins!: number;
  garage!: Garage;
}