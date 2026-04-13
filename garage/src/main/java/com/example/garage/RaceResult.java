package com.example.garage;

public class RaceResult {

    private boolean playerWon;
    private String  playerName;
    private String  opponentName;
    private String  raceName;
    private int     straightLine;
    private int     corner;
    private double  playerScore;
    private double  opponentScore;
    private long    creditsEarned;
    private long    newBalance;
    private String  resultMessage;

    // Réputation
    private int     reputationEarned;
    private int     newReputation;

    // Usure après la course
    private double  newTireWear;
    private double  newOilQuality;

    // Gang / progression
    private boolean gangMemberDefeated;
    private boolean bossDefeated;
    private boolean parkingUnlocked;
    private String  unlockedParkingName;

    // Pari voiture
    private boolean carWager;
    private String  wonCarName;
    private boolean lostCar;

    // Adversaire spécial
    private boolean specialCarUnlocked;
    private String  specialCarName;
    private long    specialCarId;

    // Difficulté & multiplicateur
    private int    difficulty;
    private String difficultyLabel;
    private double betMultiplier;
    private long   actualGain;   // gain réel après multiplicateur

    public boolean isPlayerWon()                    { return playerWon; }
    public void    setPlayerWon(boolean v)          { this.playerWon = v; }
    public String  getPlayerName()                  { return playerName; }
    public void    setPlayerName(String v)          { this.playerName = v; }
    public String  getOpponentName()                { return opponentName; }
    public void    setOpponentName(String v)        { this.opponentName = v; }
    public String  getRaceName()                    { return raceName; }
    public void    setRaceName(String v)            { this.raceName = v; }
    public int     getStraightLine()                { return straightLine; }
    public void    setStraightLine(int v)           { this.straightLine = v; }
    public int     getCorner()                      { return corner; }
    public void    setCorner(int v)                 { this.corner = v; }
    public double  getPlayerScore()                 { return playerScore; }
    public void    setPlayerScore(double v)         { this.playerScore = v; }
    public double  getOpponentScore()               { return opponentScore; }
    public void    setOpponentScore(double v)       { this.opponentScore = v; }
    public long    getCreditsEarned()               { return creditsEarned; }
    public void    setCreditsEarned(long v)         { this.creditsEarned = v; }
    public long    getNewBalance()                  { return newBalance; }
    public void    setNewBalance(long v)            { this.newBalance = v; }
    public String  getResultMessage()               { return resultMessage; }
    public void    setResultMessage(String v)       { this.resultMessage = v; }
    public int     getReputationEarned()            { return reputationEarned; }
    public void    setReputationEarned(int v)       { this.reputationEarned = v; }
    public int     getNewReputation()               { return newReputation; }
    public void    setNewReputation(int v)          { this.newReputation = v; }
    public double  getNewTireWear()                 { return newTireWear; }
    public void    setNewTireWear(double v)         { this.newTireWear = v; }
    public double  getNewOilQuality()               { return newOilQuality; }
    public void    setNewOilQuality(double v)       { this.newOilQuality = v; }
    public boolean isGangMemberDefeated()           { return gangMemberDefeated; }
    public void    setGangMemberDefeated(boolean v) { this.gangMemberDefeated = v; }
    public boolean isBossDefeated()                 { return bossDefeated; }
    public void    setBossDefeated(boolean v)       { this.bossDefeated = v; }
    public boolean isParkingUnlocked()              { return parkingUnlocked; }
    public void    setParkingUnlocked(boolean v)    { this.parkingUnlocked = v; }
    public String  getUnlockedParkingName()         { return unlockedParkingName; }
    public void    setUnlockedParkingName(String v) { this.unlockedParkingName = v; }
    public boolean isCarWager()                     { return carWager; }
    public void    setCarWager(boolean v)           { this.carWager = v; }
    public String  getWonCarName()                  { return wonCarName; }
    public void    setWonCarName(String v)          { this.wonCarName = v; }
    public boolean isLostCar()                      { return lostCar; }
    public void    setLostCar(boolean v)            { this.lostCar = v; }
    public boolean isSpecialCarUnlocked()           { return specialCarUnlocked; }
    public void    setSpecialCarUnlocked(boolean v) { this.specialCarUnlocked = v; }
    public String  getSpecialCarName()              { return specialCarName; }
    public void    setSpecialCarName(String v)      { this.specialCarName = v; }
    public long    getSpecialCarId()                { return specialCarId; }
    public void    setSpecialCarId(long v)          { this.specialCarId = v; }
    public int     getDifficulty()                  { return difficulty; }
    public void    setDifficulty(int v)             { this.difficulty = v; }
    public String  getDifficultyLabel()             { return difficultyLabel; }
    public void    setDifficultyLabel(String v)     { this.difficultyLabel = v; }
    public double  getBetMultiplier()               { return betMultiplier; }
    public void    setBetMultiplier(double v)       { this.betMultiplier = v; }
    public long    getActualGain()                  { return actualGain; }
    public void    setActualGain(long v)            { this.actualGain = v; }
}
