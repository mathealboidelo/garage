export class Car {
  id!: number;
  name!: string;
  power!: number;
  gripModifier!: number;
  weight!: number;
  aspiration!: string;
  tireType!: string;
  tireModel!: string;   // Street | Sport | Racing_Soft | Racing_Medium | etc.
  price!: number;
  tireWear: number = 100;
  oilQuality: number = 100;
  racesCount: number = 0;
}
