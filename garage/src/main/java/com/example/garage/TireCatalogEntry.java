package com.example.garage;

public class TireCatalogEntry {
    public final String model, label, category, description;
    public final double gripBonus, wearRateMultiplier;
    public final int    upgradeLevel;
    public final long   buyCost, changeCost;

    public TireCatalogEntry(String model, String label, String category,
            double gripBonus, double wearRateMultiplier, int upgradeLevel,
            long buyCost, long changeCost, String description) {
        this.model = model; this.label = label; this.category = category;
        this.gripBonus = gripBonus; this.wearRateMultiplier = wearRateMultiplier;
        this.upgradeLevel = upgradeLevel; this.buyCost = buyCost;
        this.changeCost = changeCost; this.description = description;
    }
}
