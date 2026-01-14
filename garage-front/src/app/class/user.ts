import { Garage } from "./garage";

export class User {
    id!: number;
    username!: string;
    credits!: number;
    level!: number;

    garage!: Garage
}
