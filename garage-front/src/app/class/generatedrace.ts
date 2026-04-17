export class GeneratedRace {
  raceId!: number;
  raceName!: string;
  straightLine!: number;
  corner!: number;
  difficulty!: number;
  difficultyLabel!: string;
  betMultiplier!: number;
  segments?: string;           // ex: 'S:400,C:90,S:300,C:60'
  difficultyReason!: string;
  maxBet!: number;
  opponentRefusalMsg?: string;
}