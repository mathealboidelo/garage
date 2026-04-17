export class CarUpgrade {
  id!: number;
  engineLevel!: number;
  transmissionLevel!: number;
  suspensionLevel!: number;
  brakesLevel!: number;
  weightLevel!: number;
  tiresLevel!: number;
}

export class UpgradeResult {
  success!: boolean;
  message!: string;
  costPaid!: number;
  newBalance!: number;

  powerBefore!: number;
  powerAfter!: number;
  weightBefore!: number;
  weightAfter!: number;
  gripBefore!: number;
  gripAfter!: number;
  tireBefore!: string;
  tireAfter!: string;

  engineLevel!: number;
  transmissionLevel!: number;
  suspensionLevel!: number;
  brakesLevel!: number;
  weightLevel!: number;
  tiresLevel!: number;
}

export type UpgradeCategory = 'ENGINE' | 'TRANSMISSION' | 'SUSPENSION' | 'BRAKES' | 'WEIGHT' | 'TIRES';
