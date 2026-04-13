import { Car } from "./car";

export class Racers {
  id!: number;
  name!: string;
  displayName!: string;
  car!: Car;
  prefix!: string;
  gangMember!: boolean;
  boss!: boolean;
  gangName!: string;
  special!: boolean;
  reputationRequired!: number;
  defeated!: boolean;      // rempli par ParkingView
  isGang!: boolean;        // rempli par ParkingView
  isBoss!: boolean;        // rempli par ParkingView
  carName!: string;
  carPower!: number;
  carAspiration!: string;
  carTireType!: string;
  carGripModifier!: number;
  specialCarId?: number;
}
