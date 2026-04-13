package com.example.garage;

/**
 * Réponse après un achat d'upgrade.
 */
public class UpgradeResult {

    private boolean success;
    private String  message;
    private long    costPaid;
    private long    newBalance;

    // Stats avant/après
    private int    powerBefore;
    private int    powerAfter;
    private int    weightBefore;
    private int    weightAfter;
    private double gripBefore;
    private double gripAfter;
    private String tireBefore;
    private String tireAfter;

    // Niveaux mis à jour (pour rafraîchir le front sans recharger)
    private int engineLevel;
    private int transmissionLevel;
    private int suspensionLevel;
    private int brakesLevel;
    private int weightLevel;
    private int tiresLevel;

    // ── Builder-style setters ────────────────────────────

    public boolean isSuccess()          { return success; }
    public void setSuccess(boolean v)   { this.success = v; }

    public String getMessage()          { return message; }
    public void setMessage(String v)    { this.message = v; }

    public long getCostPaid()           { return costPaid; }
    public void setCostPaid(long v)     { this.costPaid = v; }

    public long getNewBalance()         { return newBalance; }
    public void setNewBalance(long v)   { this.newBalance = v; }

    public int getPowerBefore()         { return powerBefore; }
    public void setPowerBefore(int v)   { this.powerBefore = v; }

    public int getPowerAfter()          { return powerAfter; }
    public void setPowerAfter(int v)    { this.powerAfter = v; }

    public int getWeightBefore()        { return weightBefore; }
    public void setWeightBefore(int v)  { this.weightBefore = v; }

    public int getWeightAfter()         { return weightAfter; }
    public void setWeightAfter(int v)   { this.weightAfter = v; }

    public double getGripBefore()       { return gripBefore; }
    public void setGripBefore(double v) { this.gripBefore = v; }

    public double getGripAfter()        { return gripAfter; }
    public void setGripAfter(double v)  { this.gripAfter = v; }

    public String getTireBefore()       { return tireBefore; }
    public void setTireBefore(String v) { this.tireBefore = v; }

    public String getTireAfter()        { return tireAfter; }
    public void setTireAfter(String v)  { this.tireAfter = v; }

    public int getEngineLevel()             { return engineLevel; }
    public void setEngineLevel(int v)       { this.engineLevel = v; }

    public int getTransmissionLevel()       { return transmissionLevel; }
    public void setTransmissionLevel(int v) { this.transmissionLevel = v; }

    public int getSuspensionLevel()         { return suspensionLevel; }
    public void setSuspensionLevel(int v)   { this.suspensionLevel = v; }

    public int getBrakesLevel()             { return brakesLevel; }
    public void setBrakesLevel(int v)       { this.brakesLevel = v; }

    public int getWeightLevelVal()          { return weightLevel; }
    public void setWeightLevelVal(int v)    { this.weightLevel = v; }

    public int getTiresLevel()              { return tiresLevel; }
    public void setTiresLevel(int v)        { this.tiresLevel = v; }
}
