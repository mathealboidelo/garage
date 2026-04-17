package com.example.garage;

/**
 * Course générée aléatoirement pour un défi.
 * Retournée par /api/race/generate avant de lancer la course.
 */
public class GeneratedRace {

    private long   raceId;
    private String raceName;
    private int    straightLine;   // % ligne droite (0-100)
    private int    corner;         // % virage      (0-100)

    // Difficulté calculée (1-10)
    private int    difficulty;
    private String difficultyLabel;  // "Facile", "Moyen", "Difficile", "Extrême"

    // Multiplicateur de mise (ex: 1.2 → gain ×1.2 si victoire)
    private double betMultiplier;

    // Pourquoi c'est difficile (info contextuelle)
    private String segments;          // ex: "S:400,C:90,S:300,C:60"
    private String difficultyReason;
	private long maxBet;
	private String opponentRefusalMsg;

    public long   getRaceId()                        { return raceId; }
    public void   setRaceId(long v)                  { this.raceId = v; }
    public String getRaceName()                      { return raceName; }
    public void   setRaceName(String v)              { this.raceName = v; }
    public int    getStraightLine()                  { return straightLine; }
    public void   setStraightLine(int v)             { this.straightLine = v; }
    public int    getCorner()                        { return corner; }
    public void   setCorner(int v)                   { this.corner = v; }
    public int    getDifficulty()                    { return difficulty; }
    public void   setDifficulty(int v)               { this.difficulty = v; }
    public String getDifficultyLabel()               { return difficultyLabel; }
    public void   setDifficultyLabel(String v)       { this.difficultyLabel = v; }
    public double getBetMultiplier()                 { return betMultiplier; }
    public void   setBetMultiplier(double v)         { this.betMultiplier = v; }
    public long   getMaxBet()                        { return maxBet; }
    public void   setMaxBet(long v)                  { this.maxBet = v; }
    public String getOpponentRefusalMsg()            { return opponentRefusalMsg; }
    public void   setOpponentRefusalMsg(String v)    { this.opponentRefusalMsg = v; }
    public String getDifficultyReason()              { return difficultyReason; }
    public void   setDifficultyReason(String v)      { this.difficultyReason = v; }
	public String getSegments() {
		return segments;
	}
	public void setSegments(String segments) {
		this.segments = segments;
	}
}
